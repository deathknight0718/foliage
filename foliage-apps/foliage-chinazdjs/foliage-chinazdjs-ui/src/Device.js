import React, { useState, useCallback, useContext, useEffect } from "react";
import * as _ from "underscore";
import { useNavigate } from "react-router-dom";
import { Container, Typography, Paper, Box, Stack } from "@mui/material";
import { TextField } from "@mui/material";
import { LinearProgress, Collapse } from "@mui/material";
import { Alert, AlertTitle } from "@mui/material";
import { Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import { Button, IconButton } from "@mui/material";
import { Card, CardContent, CardActions } from "@mui/material";
import { AppBar, Toolbar } from "@mui/material";
import { Search, ArrowBackIosNew, Feed, Refresh } from "@mui/icons-material";
import { AxiosContext } from "./Context";

function Header() {
  const navigate = useNavigate();
  const onClick = useCallback(() => navigate(`/${process.env.REACT_APP_NAME}/`, { replace: true }), [navigate])
  return (
    <AppBar position="fixed">
      <Toolbar>
        <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={{ mr: 2 }} onClick={onClick}>
          <ArrowBackIosNew />
        </IconButton>
        <Typography>设备监控</Typography>
      </Toolbar>
    </AppBar>
  );
}

function Footer(props) {
  const { onRegister, onSearch, onReload } = props;
  return (
    <Paper sx={{ position: "fixed", bottom: 0, left: 0, right: 0 }} elevation={3}>
      <BottomNavigation>
        <BottomNavigationAction value="feed" icon={<Feed />} onClick={onRegister} />
        <BottomNavigationAction value="search" icon={<Search />} onClick={onSearch} />
        <BottomNavigationAction value="reload" icon={<Refresh />} onClick={onReload} />
      </BottomNavigation>
    </Paper>
  );
}

function DeviceSearch(props) {
  const { open, onClose, devices, onDevices } = props;
  const [devcode, setDevcode] = useState("");
  const handleSubmit = () => {
    const result = _.find(devices, device => device.devcode.includes(devcode));
    onDevices([result]);
    onClose();
  };
  const handleCancel = () => {
    setDevcode("");
    onClose();
  };
  return (
    <Dialog open={open}>
      <DialogTitle>设备查询</DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2}>
          <TextField autoFocus margin="dense" label="识别码" fullWidth value={devcode} onChange={(event) => setDevcode(event.target.value)} />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel}>取消</Button>
        <Button onClick={handleSubmit}>查询</Button>
      </DialogActions>
    </Dialog>
  );
}

function DeviceForm(props) {
  const axios = useContext(AxiosContext);
  const { open, onClose } = props;
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState({ devcode: '', name: '', message: null });
  const handleSubmit = async () => {
    setLoading(true);
    axios.put(`/api/v1/device/register`, { ...data })
      .then((response) => {
        setData({ devcode: '', name: '', message: null });
      })
      .then(() => setLoading(false))
      .then(() => onClose())
      .catch((e) => {
        setData({ ...data, message: e.response.data })
        setLoading(false);
      });
  };
  const handleCancel = () => {
    setData({ devcode: "", name: "", message: null });
    onClose();
  };
  return (
    <Dialog open={open}>
      <DialogTitle>设备注册</DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2}>
          <Collapse in={!!data.message}>
            <Alert severity="error">
              <AlertTitle>错误</AlertTitle>
              {data.message}
            </Alert>
          </Collapse>
          <TextField autoFocus margin="dense" label="识别码" fullWidth value={data.devcode} onChange={(event) => setData({ ...data, devcode: event.target.value })} />
          <TextField margin="dense" label="别名" fullWidth value={data.name} onChange={(event) => setData({ ...data, name: event.target.value })} />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel}>取消</Button>
        <Button onClick={handleSubmit}>注册</Button> 
      </DialogActions>
      {loading && <LinearProgress />}
    </Dialog>
  );
}

function DeviceCard(props) {
  const { device: { id, name, devcode }, onReload } = props;
  const axios = useContext(AxiosContext);
  const navigate = useNavigate();
  const onGeographic = useCallback(() => navigate(`/${process.env.REACT_APP_NAME}/geographic/${id}`, { replace: true }), [navigate, id]);
  const onSpecification = useCallback(() => navigate(`/${process.env.REACT_APP_NAME}/specification/${id}`, { replace: true }), [navigate, id]);
  const onDelete = () => {
    axios.delete(`api/v1/device/${id}`)
      .then(() => onReload());
  };
  return (
    <Card sx={{ backgroundColor: "#FAFAFA" }}>
      <CardContent>
        <Typography sx={{ fontSize: 14, mb: 1.5 }}>{name}</Typography>
        <Typography sx={{ fontSize: 12 }}>{devcode}</Typography>
      </CardContent>
      <CardActions>
        <Button size="small" onClick={onSpecification}>详细信息</Button>
        <Button size="small" onClick={onGeographic}>地理信息</Button>
        <Button size="small" onClick={onDelete}>删除设备</Button>
      </CardActions>
    </Card>
  );
}

function useEffectOfDevices(submitting, reloading, devices, onDevices) {
  const axios = useContext(AxiosContext);
  return useEffect(() => {
    if (!submitting) axios.get(`/api/v1/device/query-by-paging?offset=0&limit=500`).then((response) => onDevices([...response.data]));
  }, [axios, submitting, reloading, onDevices]);
}

function DeviceList(props) {
  const { devices, onDevices, submitting, reloading, onReload } = props;
  useEffectOfDevices(submitting, reloading, devices, onDevices);
  return (
    <Container sx={{ pt: 9, pb: 2, px: 2 }}>
      <Stack spacing={1}>{devices.map((device) => <DeviceCard key={`card-${device.id}`} device={device} onReload={onReload} />)}</Stack>
    </Container>
  );
}

export default function BaiList(props) {
  const [devices, setDevices] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [searching, setSearching] = useState(false);
  const [reloading, setReloading] = useState(Date.now());
  return (
    <Box>
      <Header />
      <DeviceList submitting={submitting} devices={devices} onDevices={setDevices} reloading={reloading} onReload={() => setReloading(Date.now())} />
      <Footer onRegister={() => setSubmitting(true)} onSearch={() => setSearching(true)} onReload={() => setReloading(Date.now())} />
      <DeviceForm open={submitting} onClose={() => setSubmitting(false)} />
      <DeviceSearch open={searching} onClose={() => setSearching(false)} devices={devices} onDevices={setDevices} />
    </Box>
  );
}
