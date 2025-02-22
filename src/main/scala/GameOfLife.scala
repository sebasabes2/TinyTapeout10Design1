import chisel3._
import scala.util.Random

class GameOfLife() extends Module {
  val io = IO(new Bundle {
    val update = Input(Bool())
    val cells = Output(Vec(48, Bool()))
  })

  val cells = Reg(Vec (48, Bool()))
  for (i <- 0 until cells.length) {
    when (reset.asBool()) {
      cells(i) := Random.nextBoolean().B
    } .elsewhen (io.update) {
      cells(i) := ~cells(i)
    }
  }

  io.cells := cells
}
