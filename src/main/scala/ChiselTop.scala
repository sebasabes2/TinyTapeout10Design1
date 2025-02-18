import chisel3._

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

  // io.uio_out := 0.U
  // // use bi-directionals as input
  // io.uio_oe := 0.U

  // val add = WireDefault(0.U(7.W))
  // add := io.ui_in + io.uio_in

  // // Blink with 1 Hz
  // val cntReg = RegInit(0.U(32.W))
  // val ledReg = RegInit(0.U(1.W))
  // cntReg := cntReg + 1.U
  // when (cntReg === 25000000.U) {
  //   cntReg := 0.U
  //   ledReg := ~ledReg
  // }
  // io.uo_out := ledReg ## add

  // Modified:

  val vga = Module(new VGA())
  io.uio_out := vga.io.output(15,8)
  io.uo_out := vga.io.output(7,0)
  // enable bidirectional output:
  io.uio_oe := 0xff.U
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}