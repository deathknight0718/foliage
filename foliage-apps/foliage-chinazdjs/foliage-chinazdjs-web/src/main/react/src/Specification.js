import * as echarts from "echarts";
import React, { useState, useCallback, useEffect, useRef, useContext } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { Container, Typography, Box, Stack, Grid } from "@mui/material";
import { Switch } from "@mui/material";
import { ToggleButtonGroup, ToggleButton } from "@mui/material";
import { IconButton } from "@mui/material";
import { Card, CardContent } from "@mui/material";
import { AppBar, Toolbar } from "@mui/material";
import { LinearProgress } from "@mui/material";
import { ArrowBackIosNew } from "@mui/icons-material";
import { AxiosContext } from "./Context";

const { REACT_APP_NAME } = process.env;

function Header(props) {
  const { loading } = props;
  const navigate = useNavigate();
  const onClick = useCallback(() => navigate(`/${REACT_APP_NAME}/device`, { replace: true }), [navigate])
  return (
    <AppBar position="fixed">
      <Toolbar>
        <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={{ mr: 2 }} onClick={onClick}><ArrowBackIosNew /></IconButton>
        <Typography>设备信息</Typography>
      </Toolbar>
      {loading && <LinearProgress />}
    </AppBar>
  );
}

function Gauge(props) {
  const { value, max, name } = props;
  const [chart, setChart] = useState(null);
  const ref = useRef();
  useEffect(() => {
    if (!ref.current) return;
    if (!chart) {
      const dom = ref.current;
      const echart = echarts.init(dom);
      setChart(echart);
      return;
    }
    const option = {
      series: [{
        name: "Pressure",
        type: "gauge",
        max,
        axisLine: { lineStyle: { width: 7 } },
        axisTick: { show: false },
        axisLabel: { show: false },
        splitLine: { show: false },
        detail: { formatter: "{value}", fontSize: 10, offsetCenter: [0, "60%"] },
        title: { fontSize: 12, offsetCenter: [0, "120%"] },
        data: [{ value, name }],
        progress: { show: true, width: 7 },
      }]
    };
    chart.setOption(option);
  }, [value, max, name, chart, setChart]);
  return <div ref={ref} style={{ width: "100%", height: "100%" }}></div>;
}

function DeviceCards(props) {
  const { data } = props;
  const { 手柄前进开关输入状态: c1v1, 手柄后退开关输入状态: c1v2, 手柄中位开关输入状态: c1v3 } = data;
  const { 发动机转速: c2v1, 水温: c2v2, 机油压力: c2v3, 行车速度: c2v4, 振动频率: c2v5 } = data;
  const { 发动机水温高报警: c3v1, 补油压力报警: c3v2, 空滤阻塞报警: c3v3, 液压真空度报警: c3v4 } = data;
  const { 行走泵前进比例阀输出: c4v1, 行走泵后退比例阀输出: c4v2, 振动泵正转比例阀输出: c4v3, 振动泵反转比例阀输出: c4v4 } = data;
  return (
    <Container sx={{ pt: 9, pb: 2, px: 2 }}>
      <Stack spacing={1}>
        <Card sx={{ backgroundColor: "#FAFAFA" }}>
          <CardContent>
            <Typography sx={{ fontSize: 14, mb: 1.5 }}>设备状态</Typography>
            <ToggleButtonGroup color="primary" value={[`c1v1${c1v1}`, `c1v2${c1v2}`, `c1v3${c1v3}`]} size="small">
              <ToggleButton value="c1v11">前进</ToggleButton>
              <ToggleButton value="c1v21">后退</ToggleButton>
              <ToggleButton value="c1v31">停止</ToggleButton>
            </ToggleButtonGroup>
          </CardContent>
        </Card>
        <Card sx={{ backgroundColor: "#FAFAFA" }}>
          <CardContent>
            <Typography sx={{ fontSize: 14, mb: 1.5 }}>运行参数</Typography>
            <Grid container spacing={1}>
              <Grid item xs={4} style={{ textAlign: "center" }}><Gauge value={Number(c2v1)} max={5000} name={"发动机转速"} /></Grid>
              <Grid item xs={4} style={{ textAlign: "center" }}><Gauge value={Number(c2v2)} max={100} name={"水温"} /></Grid>
              <Grid item xs={4} style={{ textAlign: "center" }}><Gauge value={Number(c2v3)} max={2} name={"机油压力"}/></Grid>
              <Grid item xs={4} style={{ textAlign: "center" }}><Gauge value={Number(c2v4)} max={150} name={"行驶速度"} /></Grid>
              <Grid item xs={4} style={{ textAlign: "center" }}><Gauge value={Number(c2v5)} max={200} name={"振动频率"} /></Grid>
            </Grid>
          </CardContent>
        </Card>
        <Card sx={{ backgroundColor: "#FAFAFA" }}>
          <CardContent>
            <Typography sx={{ fontSize: 14, mb: 1.5 }}>报警状态</Typography>
            <Grid container spacing={1}>
              <Grid item xs={6} style={{ textAlign: "center" }}><Switch color="error" disabled checked={c3v1 === "1"} /></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Switch color="error" disabled checked={c3v2 === "1"} /></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Typography variant="caption" display="block" gutterBottom>水温过高预警</Typography></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Typography variant="caption" display="block" gutterBottom>机油压力预警</Typography></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Switch color="error" disabled checked={c3v3 === "1"} /></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Switch color="error" disabled checked={c3v4 === "1"} /></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Typography variant="caption" display="block" gutterBottom>空滤阻塞预警</Typography></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Typography variant="caption" display="block" gutterBottom>液压真空预警</Typography></Grid>
            </Grid>
          </CardContent>
        </Card>
        <Card sx={{ backgroundColor: "#FAFAFA" }}>
          <CardContent>
            <Typography sx={{ fontSize: 14, mb: 1.5 }}>控制参数</Typography>
            <Grid container spacing={1}>
              <Grid item xs={6} style={{ textAlign: "center" }}><Gauge value={Number(c4v1)} max={800} name={"前行走电流"} /></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Gauge value={Number(c4v2)} max={800} name={"后行走电流"} /></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Gauge value={Number(c4v3)} max={800} name={"前振动电流"}/></Grid>
              <Grid item xs={6} style={{ textAlign: "center" }}><Gauge value={Number(c4v4)} max={800} name={"后振动电流"} /></Grid>
            </Grid>
          </CardContent>
        </Card>
      </Stack>
    </Container>
  );
}

export default function Specification() {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState({});
  const { id } = useParams();
  const axios = useContext(AxiosContext);
  useEffect(() => {
    axios.get(`api/v1/specification/${id}`)
      .then((response) => {
        const { data } = response.data;
        setData(data);
      })
      .then(() => setLoading(false));
  }, [axios, id, setLoading]);
  return (
    <Box>
      <Header loading={loading} />
      {!loading && <DeviceCards data={data} />}
    </Box>
  );
}
