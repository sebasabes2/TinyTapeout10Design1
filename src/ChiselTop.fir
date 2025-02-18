circuit ChiselTop :
  module VGA :
    input clock : Clock
    input reset : Reset
    output io : { output : UInt<16>}

    reg hcounter : UInt<10>, clock with :
      reset => (reset, UInt<10>("h0")) @[VGA.scala 21:25]
    reg vcounter : UInt<10>, clock with :
      reset => (reset, UInt<10>("h0")) @[VGA.scala 22:25]
    node _T = lt(hcounter, UInt<10>("h31f")) @[VGA.scala 24:18]
    when _T : @[VGA.scala 24:42]
      node _hcounter_T = add(hcounter, UInt<1>("h1")) @[VGA.scala 25:26]
      node _hcounter_T_1 = tail(_hcounter_T, 1) @[VGA.scala 25:26]
      hcounter <= _hcounter_T_1 @[VGA.scala 25:14]
    else :
      hcounter <= UInt<1>("h0") @[VGA.scala 27:14]
      node _T_1 = lt(vcounter, UInt<10>("h20c")) @[VGA.scala 28:20]
      when _T_1 : @[VGA.scala 28:45]
        node _vcounter_T = add(vcounter, UInt<1>("h1")) @[VGA.scala 29:28]
        node _vcounter_T_1 = tail(_vcounter_T, 1) @[VGA.scala 29:28]
        vcounter <= _vcounter_T_1 @[VGA.scala 29:16]
      else :
        vcounter <= UInt<1>("h0") @[VGA.scala 31:16]
    node _hsync_T = lt(hcounter, UInt<10>("h290")) @[VGA.scala 35:25]
    node _hsync_T_1 = geq(hcounter, UInt<10>("h2f0")) @[VGA.scala 35:76]
    node hsync = or(_hsync_T, _hsync_T_1) @[VGA.scala 35:63]
    node _vsync_T = lt(vcounter, UInt<9>("h1ea")) @[VGA.scala 36:25]
    node _vsync_T_1 = geq(vcounter, UInt<9>("h1ec")) @[VGA.scala 36:76]
    node vsync = or(_vsync_T, _vsync_T_1) @[VGA.scala 36:63]
    node _visible_T = lt(hcounter, UInt<10>("h280")) @[VGA.scala 37:27]
    node _visible_T_1 = lt(vcounter, UInt<9>("h1e0")) @[VGA.scala 37:60]
    node visible = and(_visible_T, _visible_T_1) @[VGA.scala 37:47]
    wire data : UInt<1>[16] @[VGA.scala 39:18]
    data[0] <= UInt<1>("h0") @[VGA.scala 42:13]
    data[1] <= UInt<1>("h0") @[VGA.scala 42:13]
    data[2] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[3] <= UInt<1>("h0") @[VGA.scala 42:13]
    data[4] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[5] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[6] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[7] <= UInt<1>("h0") @[VGA.scala 42:13]
    data[8] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[9] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[10] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[11] <= UInt<1>("h0") @[VGA.scala 42:13]
    data[12] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[13] <= UInt<1>("h1") @[VGA.scala 42:13]
    data[14] <= UInt<1>("h0") @[VGA.scala 42:13]
    data[15] <= UInt<1>("h1") @[VGA.scala 42:13]
    node _index_T = bits(vcounter, 7, 6) @[VGA.scala 46:23]
    node _index_T_1 = bits(hcounter, 7, 6) @[VGA.scala 46:40]
    node index = cat(_index_T, _index_T_1) @[VGA.scala 46:29]
    node _color_T = bits(hcounter, 9, 8) @[VGA.scala 48:35]
    node _color_T_1 = eq(_color_T, UInt<1>("h0")) @[VGA.scala 48:41]
    node _color_T_2 = and(data[index], _color_T_1) @[VGA.scala 48:23]
    node _color_T_3 = bits(vcounter, 9, 8) @[VGA.scala 48:62]
    node _color_T_4 = eq(_color_T_3, UInt<1>("h0")) @[VGA.scala 48:68]
    node _color_T_5 = and(_color_T_2, _color_T_4) @[VGA.scala 48:50]
    node color = and(_color_T_5, visible) @[VGA.scala 48:77]
    node _r_T = cat(color, color) @[VGA.scala 49:17]
    node _r_T_1 = cat(_r_T, color) @[VGA.scala 49:26]
    node r = cat(_r_T_1, color) @[VGA.scala 49:35]
    node _g_T = cat(color, color) @[VGA.scala 50:17]
    node _g_T_1 = cat(_g_T, color) @[VGA.scala 50:26]
    node g = cat(_g_T_1, color) @[VGA.scala 50:35]
    node _b_T = cat(color, color) @[VGA.scala 51:17]
    node _b_T_1 = cat(_b_T, color) @[VGA.scala 51:26]
    node b = cat(_b_T_1, color) @[VGA.scala 51:35]
    node _io_output_T = cat(UInt<1>("h0"), UInt<1>("h0")) @[VGA.scala 53:19]
    node _io_output_T_1 = cat(_io_output_T, vsync) @[VGA.scala 53:25]
    node _io_output_T_2 = cat(_io_output_T_1, hsync) @[VGA.scala 53:34]
    node _io_output_T_3 = cat(_io_output_T_2, g) @[VGA.scala 53:43]
    node _io_output_T_4 = cat(_io_output_T_3, b) @[VGA.scala 53:48]
    node _io_output_T_5 = cat(_io_output_T_4, r) @[VGA.scala 53:53]
    io.output <= _io_output_T_5 @[VGA.scala 53:13]

  module ChiselTop :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip ui_in : UInt<8>, uo_out : UInt<8>, flip uio_in : UInt<8>, uio_out : UInt<8>, uio_oe : UInt<8>}

    inst vga of VGA @[ChiselTop.scala 35:19]
    vga.clock <= clock
    vga.reset <= reset
    node _io_uio_out_T = bits(vga.io.output, 15, 8) @[ChiselTop.scala 36:30]
    io.uio_out <= _io_uio_out_T @[ChiselTop.scala 36:14]
    node _io_uo_out_T = bits(vga.io.output, 7, 0) @[ChiselTop.scala 37:29]
    io.uo_out <= _io_uo_out_T @[ChiselTop.scala 37:13]
    io.uio_oe <= UInt<8>("hff") @[ChiselTop.scala 39:13]

