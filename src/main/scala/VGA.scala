import chisel3._
import scala.util.Random

class VGA() extends Module {
  val io = IO(new Bundle {
    val data = Input(Vec(48, Bool()))
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

  val hcounter = RegInit(0.U(10.W))
  val vcounter = RegInit(0.U(10.W))

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
  val inFrame = (hcounter < H_VISIBLE_AREA.U) && (vcounter < V_VISIBLE_AREA.U)

  val xi = (hcounter / 80.U)(2,0)
  val yi = (vcounter / 80.U)(2,0)
  val index = yi ## xi
  print(xi, yi, index)
  val current = io.data(index)
  val color = current && inFrame
  val r = color ## color
  val g = color ## color
  val b = color ## color
  io.output := hsync ## b(0) ## g(0) ## r(0) ## vsync ## b(1) ## g(1) ## r(1)
}
