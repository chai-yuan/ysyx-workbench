module sdram(
  input [ 3:0] sel,

  input        clk,
  input        cke,
  input        cs,
  input        ras,
  input        cas,
  input        we,
  input [12:0] a,
  input [ 1:0] ba,
  input [ 1:0] dqm,
  inout [15:0] dq
);

// 拓展存在问题，全部改为位拓展
MT48LC16M16A2 ram0(
.clk(clk),
.cke(cke),
.cs(cs | ~sel[0]),
.ras(ras),
.cas(cas),
.we(we),
.a(a[12:0]),
.ba(ba),
.dqm(dqm[1:0]),
.dq(dq[15:0])
);
MT48LC16M16A2 ram1(
.clk(clk),
.cke(cke),
.cs(cs | ~sel[1]),
.ras(ras),
.cas(cas),
.we(we),
.a(a[12:0]),
.ba(ba),
.dqm(dqm[1:0]),
.dq(dq[15:0])
);
MT48LC16M16A2 ram2(
.clk(clk),
.cke(cke),
.cs(cs | ~sel[2]),
.ras(ras),
.cas(cas),
.we(we),
.a(a[12:0]),
.ba(ba),
.dqm(dqm[1:0]),
.dq(dq[15:0])
);
MT48LC16M16A2 ram3(
.clk(clk),
.cke(cke),
.cs(cs | ~sel[3]),
.ras(ras),
.cas(cas),
.we(we),
.a(a[12:0]),
.ba(ba),
.dqm(dqm[1:0]),
.dq(dq[15:0])
);

endmodule
