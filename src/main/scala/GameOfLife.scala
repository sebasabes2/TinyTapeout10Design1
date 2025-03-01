import chisel3._
import scala.util.Random

class GameOfLife() extends Module {
  val io = IO(new Bundle {
    val update = Input(Bool())
    val pixel = new Pixel
    val color = Output(Bool())
  })

  val cells = Reg(Vec (48, Bool()))
  for (i <- 0 until cells.length) {
    when (reset.asBool()) {
      cells(i) := Random.nextBoolean().B
    } .elsewhen (io.update) {
      cells(i) := ~cells(i)
    }
  }

  val xi = (io.pixel.x / 80.U)(2,0)
  val yi = (io.pixel.y / 80.U)(2,0)
  val index = yi ## xi
  print(xi, yi, index)
  io.color := cells(0)
}
