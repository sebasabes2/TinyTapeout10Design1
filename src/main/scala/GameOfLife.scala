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
    // User Input
    val tick = Input(Bool())
    val set = Input(Bool())
    val state = Input(Bool())
    val coordinate = Input(UInt(6.W))
    // VGA signals
    val pixel = new Pixel
    val color = Output(Bool())
  })

  val cellMem = Module(new CellMemory)
  cellMem.io.address := DontCare
  cellMem.io.dataWrite := DontCare
  cellMem.io.writeEnabled := false.B

  val copyMem = Module(new CellMemory)
  copyMem.io.address := DontCare
  copyMem.io.dataWrite := DontCare
  copyMem.io.writeEnabled := false.B

  val displayXIndex = (io.pixel.x / 80.U)(2,0)
  val displayYIndex = (io.pixel.y / 80.U)(2,0)
  val displayIndex = displayYIndex ## displayXIndex
  io.color := ~cellMem.io.dataRead

  // Finite State Machine
  object States extends ChiselEnum {
    val reset, display, update, process, begincopy, copy, input = Value
  }
  val state = RegInit(States.reset)
  val xIndex = RegInit(0.U(3.W))
  val yIndex = RegInit(0.U(3.W))
  val updateCounter = Reg(UInt(5.W))

  switch (state) {
    is (States.reset) {
      val index = yIndex ## xIndex
      val nextIndex = index + 1.U
      yIndex := nextIndex(5,3)
      xIndex := nextIndex(2,0)
      when (nextIndex === 48.U) {
        state := States.display
      }
      cellMem.io.address := index
      cellMem.io.dataWrite := false.B
      cellMem.io.writeEnabled := true.B
    }

    is (States.display) {
      cellMem.io.address := displayIndex
      // On Rising edge of not yValid
      when (!io.pixel.yValid && RegNext(io.pixel.yValid)) {
        state := States.update
      }
    }

    is (States.update) {
      updateCounter := updateCounter + 1.U
      when (updateCounter === 0.U && io.tick) {
        xIndex := 0.U
        yIndex := 0.U
        cellMem.io.address := 0.U
        state := States.process
      } .otherwise {
        state := States.input
      }
    }

    is (States.process) {
      val index = yIndex ## xIndex
      val nextIndex = index + 1.U
      yIndex := nextIndex(5,3)
      xIndex := nextIndex(2,0)
      when (nextIndex === 48.U) {
        state := States.begincopy
      }
      cellMem.io.address := nextIndex
      copyMem.io.address := index
      copyMem.io.dataWrite := ~cellMem.io.dataRead
      copyMem.io.writeEnabled := true.B
    }

    is (States.begincopy) {
      xIndex := 0.U
      yIndex := 0.U
      copyMem.io.address := 0.U
      state := States.copy
    }

    is (States.copy) {
      val index = yIndex ## xIndex
      val nextIndex = index + 1.U
      yIndex := nextIndex(5,3)
      xIndex := nextIndex(2,0)
      when (nextIndex === 48.U) {
        state := States.input
      }
      copyMem.io.address := nextIndex
      cellMem.io.address := index
      cellMem.io.dataWrite := copyMem.io.dataRead
      cellMem.io.writeEnabled := true.B
    }

    is (States.input) {
      cellMem.io.address := io.coordinate
      cellMem.io.dataWrite := io.state
      cellMem.io.writeEnabled := io.set
      state := States.display
    }
  }

}
