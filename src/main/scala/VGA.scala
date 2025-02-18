import chisel3._
import scala.util.Random

class VGA() extends Module {
  val io = IO(new Bundle {
    val output = Output(UInt(16.W))
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
  val visible = (hcounter < H_VISIBLE_AREA.U) && (vcounter < V_VISIBLE_AREA.U)

  val data = Wire(Vec(16, Bool()))
  for (i <- 0 until 16) {
    val r = Random.nextBoolean()
    data(i) := r.B
    println(r)
  }

  val index = vcounter(7,6) ## hcounter(7,6)
  val current = data(index)
  val color = current && (hcounter(9,8) === 0.U) && (vcounter(9,8) === 0.U) && visible
  val r = color ## color ## color ## color
  val g = color ## color ## color ## color
  val b = color ## color ## color ## color
  val nc = false.B
  io.output := nc ## nc ## vsync ## hsync ## g ## b ## r
}
