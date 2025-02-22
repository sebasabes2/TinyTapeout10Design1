import chisel3._
import chisel3.util._

/**
 * Example design in Chisel.
 * A redesign of the Tiny Tapeout example.
 */
class ChiselTop() extends Module {
  val io = IO(new Bundle {
    val ui_in = Input(UInt(8.W))      // Dedicated inputs
    val uo_out = Output(UInt(8.W))    // Dedicated outputs
    val uio_in = Input(UInt(8.W))     // IOs: Input path
    val uio_out = Output(UInt(8.W))   // IOs: Output path
    val uio_oe = Output(UInt(8.W))    // IOs: Enable path (active high: 0=input, 1=output)
  })

  io.uio_out := 0.U

  val MAX_COUNT = 12500000
  val countReg = Reg(UInt(log2Up(MAX_COUNT).W))
  when (io.ui_in(0)) {
    when (countReg === MAX_COUNT.U) {
      countReg := 0.U
    } .otherwise {
      countReg := countReg + 1.U
    }
  }

  val gol = Module(new GameOfLife())
  gol.io.update := countReg === MAX_COUNT.U

  val vga = Module(new VGA())
  vga.io.data := gol.io.cells
  io.uo_out := vga.io.output
  // set bidirectional io to output:
  io.uio_oe := 0xff.U
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}
