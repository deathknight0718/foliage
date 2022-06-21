import React, { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Container, Divider, Grid } from "@mui/material";
import { Card, CardContent } from "@mui/material";
import { Stack, Typography } from "@mui/material";
import { IconButton, Button } from "@mui/material";
import { AppBar, Toolbar } from "@mui/material";
import { Menu, DeviceHub } from "@mui/icons-material";

function Header() {
  return (
    <AppBar position="fixed">
      <Toolbar>
        <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={{ mr: 2 }}><Menu /></IconButton>
        <Typography>中大工业设备移动端</Typography>
      </Toolbar>
    </AppBar>
  );
}

function Modules(props) {
  const navigate = useNavigate();
  const onClick = useCallback(() => navigate("/device", { replace: true }), [navigate])
  return (
    <Container sx={{ pt: 9, pb: 2, px: 2 }}>
      <Stack spacing={1}>
        <Card sx={{ backgroundColor: "#FAFAFA" }}>
          <CardContent>
            <Typography sx={{ fontSize: 14 }}>组件列表</Typography>
          </CardContent>
          <Divider />
          <CardContent>
            <Grid container spacing={1}>
              <Grid item xs={3} style={{ textAlign: "center" }}>
                <Button onClick={onClick}>
                  <DeviceHub />
                </Button>
              </Grid>
              <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
              <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
              <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
              <Grid item xs={3} style={{ textAlign: "center" }}>
                <Typography variant="caption">设备监控</Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      </Stack>
    </Container>
  )
}

export default function Main() {
  return (
    <Box>
      <Header />
      <Modules />
    </Box>
  );
}
