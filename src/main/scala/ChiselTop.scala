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
  
  // set bidirectional io to input:
  io.uio_oe := 0.U
  io.uio_out := 0.U

  val gol = Module(new GameOfLife())
  gol.io.coordinate := io.ui_in(5,0)
  gol.io.state := io.ui_in(6)
  gol.io.set := io.ui_in(7)
  gol.io.tick := io.uio_in(0)

  val vga = Module(new VGA())
  gol.io.pixel := vga.io.pixel
  vga.io.color := gol.io.color
  io.uo_out := vga.io.output
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}
