import chisel3._
import scala.util.Random

class Pixel extends Bundle {
  val x = Input(UInt(10.W))
  val y = Input(UInt(9.W))
  val xValid = Input(Bool())
  val yValid = Input(Bool())
}

class VGA() extends Module {
  val io = IO(new Bundle {
    val pixel = Flipped(new Pixel)
    val color = Input(Bool())
    val output = Output(UInt(8.W))
  })

  val H_VISIBLE_AREA = 640
  val H_FRONT_PORCH = 16
  val H_SYNC_PULSE = 96
  val H_BACK_PORCH = 48
  val H_WHOLE_LINE = 800

  val V_VISIBLE_AREA = 480
  val V_FRONT_PORCH = 10
  val V_SYNC_PULSE = 2
  val V_BACK_PORCH = 33
  val V_WHOLE_FRAME = 525

  val hcounter = Reg(UInt(10.W))
  val vcounter = Reg(UInt(10.W))

  when (hcounter < (H_WHOLE_LINE - 1).U) {
    hcounter := hcounter + 1.U
  } .otherwise {
    hcounter := 0.U
    when (vcounter < (V_WHOLE_FRAME - 1).U) {
      vcounter := vcounter + 1.U
    } .otherwise {
      vcounter := 0.U
    }
  }

  val hsync = (hcounter < (H_VISIBLE_AREA + H_FRONT_PORCH).U) || (hcounter >= (H_VISIBLE_AREA + H_FRONT_PORCH + H_SYNC_PULSE).U)
  val vsync = (vcounter < (V_VISIBLE_AREA + V_FRONT_PORCH).U) || (vcounter >= (V_VISIBLE_AREA + V_FRONT_PORCH + V_SYNC_PULSE).U)
  val pixelXValid = hcounter < H_VISIBLE_AREA.U
  val pixelYValid = vcounter < V_VISIBLE_AREA.U
  val inFrame = pixelXValid && pixelYValid

  io.pixel.x := hcounter
  io.pixel.y := vcounter
  io.pixel.xValid := pixelXValid
  io.pixel.yValid := pixelYValid

  // Wait for input of color, which is 1 clock cycle delayed

  val color = io.color && RegNext(inFrame)
  val r = color ## color
  val g = color ## color
  val b = color ## color
  io.output := RegNext(hsync) ## b(0) ## g(0) ## r(0) ## RegNext(vsync) ## b(1) ## g(1) ## r(1)
}
