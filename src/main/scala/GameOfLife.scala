import chisel3._
import chisel3.util._

class CellMemory() extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(6.W))
    val dataRead = Output(Bool())
    val dataWrite = Input(Bool())
    val writeEnabled = Input(Bool())
  })

  val mem = SyncReadMem(48, Bool())
  io.dataRead := DontCare
  val port = mem(io.address)
  when (io.writeEnabled) {
    port := io.dataWrite
  } .otherwise {
    io.dataRead := port
  }
}

class GameOfLife() extends Module {
  val io = IO(new Bundle {
    val update = Input(Bool())
    val pixel = new Pixel
    val color = Output(Bool())
  })

  val cellMem = Module(new CellMemory)
  cellMem.io.address := DontCare
  cellMem.io.dataWrite := DontCare
  cellMem.io.writeEnabled := false.B

  val displayXIndex = (io.pixel.x / 80.U)(2,0)
  val displayYIndex = (io.pixel.y / 80.U)(2,0)
  val displayIndex = displayYIndex ## displayXIndex
  io.color := ~cellMem.io.dataRead

  // Finite State Machine
  object States extends ChiselEnum {
    val reset, display, update = Value
  }
  val state = RegInit(States.reset)

  switch (state) {
    is (States.reset) {
      cellMem.io.address := 8.U
      cellMem.io.dataWrite := false.B
      cellMem.io.writeEnabled := true.B
      state := States.display
    }

    is (States.display) {
      cellMem.io.address := displayIndex
      // On Rising edge of not yValid
      when (!io.pixel.yValid && RegNext(io.pixel.yValid)) {
        state := States.update
      }
    }

    is (States.update) {
      cellMem.io.address := 1.U
      cellMem.io.dataWrite := true.B
      cellMem.io.writeEnabled := true.B
      state := States.display
    }
  }

}
