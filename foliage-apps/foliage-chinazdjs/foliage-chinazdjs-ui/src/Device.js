import React, { useState, useCallback } from "react";
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
import { Search, Menu, Feed } from "@mui/icons-material";

function Header() {
  return (
    <AppBar position="fixed">
      <Toolbar>
        <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={{ mr: 2 }}><Menu /></IconButton>
      </Toolbar>
    </AppBar>
  );
}

function Footer(props) {
  const { onFeed, onSearch } = props;
  return (
    <Paper sx={{ position: "fixed", bottom: 0, left: 0, right: 0 }} elevation={3}>
      <BottomNavigation>
        <BottomNavigationAction value="feed" icon={<Feed />} onClick={onFeed} />
        <BottomNavigationAction value="search" icon={<Search />} onClick={onSearch} />
      </BottomNavigation>
    </Paper>
  );
}

function MachineDialog(props) {
  const { axios, opened, onSwitching } = props;
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState({ devcode: '', name: '', message: null });
  const handleSubmit = async () => {
    setLoading(true);
    try {
      await axios.put(`/api/v1/device/register`, { ...data });
      setData({ devcode: '', name: '', message: null });
      onSwitching();
      setLoading(false);
    } catch (e) {
      setData({ ...data, message: e.message })
      setLoading(false);
    }
  };
  const handleCancel = () => {
    setData({ devcode: '', name: '', message: null });
    onSwitching();
  };
  return (
    <Dialog open={opened}>
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

function MachineList() {
  const navigate = useNavigate();
  return (
    <Container sx={{ pt: 9, pb: 2, px: 2 }}>
      <Stack spacing={1}>
        <Card sx={{ backgroundColor: "#FAFAFA" }}>
          <CardContent>
            <Typography sx={{ fontSize: 12, mb: 1.5 }} color="text.secondary">XXXXXXXXXX</Typography>
            <Typography sx={{ fontSize: 14, mb: 1.5 }}>设备名称</Typography>
            <Typography sx={{ fontSize: 12 }}>地址信息 XXXXXXXXXXXXXXXXXXXXX</Typography>
          </CardContent>
          <CardActions>
            <Button size="small">详细信息</Button>
            <Button size="small" onClick={useCallback(() => navigate("/geographic?devcode=14811212136", { replace : true }), [navigate])}>地理信息</Button>
            <Button size="small">删除设备</Button>
          </CardActions>
        </Card>
      </Stack>
    </Container>
  );
}

export default function BaiList(props) {
  const { axios } = props;
  const [dialogOpened, dialogSwitching] = useState(false);
  return (
    <Box>
      <Header />
      <MachineList />
      <Footer onFeed={() => dialogSwitching(true)} onSearch={() => dialogSwitching(true)} />
      <MachineDialog axios={axios} opened={dialogOpened} onSwitching={() => dialogSwitching(false)} />
    </Box>
  );
}
