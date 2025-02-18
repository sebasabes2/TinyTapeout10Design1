module VGA(
  input         clock,
  input         reset,
  output [15:0] io_output
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [9:0] hcounter; // @[VGA.scala 21:25]
  reg [9:0] vcounter; // @[VGA.scala 22:25]
  wire [9:0] _hcounter_T_1 = hcounter + 10'h1; // @[VGA.scala 25:26]
  wire [9:0] _vcounter_T_1 = vcounter + 10'h1; // @[VGA.scala 29:28]
  wire  hsync = hcounter < 10'h290 | hcounter >= 10'h2f0; // @[VGA.scala 35:63]
  wire  vsync = vcounter < 10'h1ea | vcounter >= 10'h1ec; // @[VGA.scala 36:63]
  wire  visible = hcounter < 10'h280 & vcounter < 10'h1e0; // @[VGA.scala 37:47]
  wire [3:0] index = {vcounter[7:6],hcounter[7:6]}; // @[VGA.scala 46:29]
  wire  _GEN_6 = 4'h3 == index ? 1'h0 : 4'h2 == index; // @[VGA.scala 48:{23,23}]
  wire  _GEN_10 = 4'h7 == index ? 1'h0 : 4'h6 == index | (4'h5 == index | (4'h4 == index | _GEN_6)); // @[VGA.scala 48:{23,23}]
  wire  _GEN_14 = 4'hb == index ? 1'h0 : 4'ha == index | (4'h9 == index | (4'h8 == index | _GEN_10)); // @[VGA.scala 48:{23,23}]
  wire  _GEN_17 = 4'he == index ? 1'h0 : 4'hd == index | (4'hc == index | _GEN_14); // @[VGA.scala 48:{23,23}]
  wire  color = (4'hf == index | _GEN_17) & hcounter[9:8] == 2'h0 & vcounter[9:8] == 2'h0 & visible; // @[VGA.scala 48:77]
  wire [3:0] r = {color,color,color,color}; // @[VGA.scala 49:35]
  wire [11:0] _io_output_T_4 = {2'h0,vsync,hsync,color,color,color,color,r}; // @[VGA.scala 53:48]
  assign io_output = {_io_output_T_4,r}; // @[VGA.scala 53:53]
  always @(posedge clock) begin
    if (reset) begin // @[VGA.scala 21:25]
      hcounter <= 10'h0; // @[VGA.scala 21:25]
    end else if (hcounter < 10'h31f) begin // @[VGA.scala 24:42]
      hcounter <= _hcounter_T_1; // @[VGA.scala 25:14]
    end else begin
      hcounter <= 10'h0; // @[VGA.scala 27:14]
    end
    if (reset) begin // @[VGA.scala 22:25]
      vcounter <= 10'h0; // @[VGA.scala 22:25]
    end else if (!(hcounter < 10'h31f)) begin // @[VGA.scala 24:42]
      if (vcounter < 10'h20c) begin // @[VGA.scala 28:45]
        vcounter <= _vcounter_T_1; // @[VGA.scala 29:16]
      end else begin
        vcounter <= 10'h0; // @[VGA.scala 31:16]
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  hcounter = _RAND_0[9:0];
  _RAND_1 = {1{`RANDOM}};
  vcounter = _RAND_1[9:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ChiselTop(
  input        clock,
  input        reset,
  input  [7:0] io_ui_in,
  output [7:0] io_uo_out,
  input  [7:0] io_uio_in,
  output [7:0] io_uio_out,
  output [7:0] io_uio_oe
);
  wire  vga_clock; // @[ChiselTop.scala 35:19]
  wire  vga_reset; // @[ChiselTop.scala 35:19]
  wire [15:0] vga_io_output; // @[ChiselTop.scala 35:19]
  VGA vga ( // @[ChiselTop.scala 35:19]
    .clock(vga_clock),
    .reset(vga_reset),
    .io_output(vga_io_output)
  );
  assign io_uo_out = vga_io_output[7:0]; // @[ChiselTop.scala 37:29]
  assign io_uio_out = vga_io_output[15:8]; // @[ChiselTop.scala 36:30]
  assign io_uio_oe = 8'hff; // @[ChiselTop.scala 39:13]
  assign vga_clock = clock;
  assign vga_reset = reset;
endmodule
