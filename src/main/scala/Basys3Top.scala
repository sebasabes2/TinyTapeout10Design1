import chisel3._

class clk_wiz_0 extends BlackBox {
  val io = IO(new Bundle {
    val reset = Input(Bool())
    val clk_in = Input(Clock())
    val clk_out = Output(Clock())
    val locked = Output(Bool())
  })
}

class Basys3Top() extends Module {
  val io = IO(new Bundle {
    val ui_in = Input(UInt(8.W))      // Dedicated inputs
    val uo_out = Output(UInt(8.W))    // Dedicated outputs
    val uio_in = Input(UInt(8.W))     // IOs: Input path
    // val uio_out = Output(UInt(8.W))   // IOs: Output path
    // val uio_oe = Output(UInt(8.W))    // IOs: Enable path (active high: 0=input, 1=output)
  })

  val clkWiz = Module(new clk_wiz_0)
  clkWiz.io.clk_in := clock
  clkWiz.io.reset := false.B

  val chiselTop = withClock(clkWiz.io.clk_out) { Module(new ChiselTop()) }
  chiselTop.io.ui_in := io.ui_in
  chiselTop.io.uio_in := io.uio_in
  io.uo_out := chiselTop.io.uo_out
}

object Basys3Top extends App {
  emitVerilog(new Basys3Top(), Array("--target-dir", "src"))
}
