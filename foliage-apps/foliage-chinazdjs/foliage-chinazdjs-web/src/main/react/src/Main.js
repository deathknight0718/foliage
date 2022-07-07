import React, { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Container, Divider, Grid } from "@mui/material";
import { Card, CardContent, CardMedia, CardActions, CardHeader } from "@mui/material";
import { Stack, Typography } from "@mui/material";
import { IconButton, Button } from "@mui/material";
import { AppBar, Toolbar } from "@mui/material";
import { Menu, DeviceHub } from "@mui/icons-material";

function Header() {
  return (
    <AppBar position="fixed">
      <Toolbar>
        <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={{ mr: 2 }}><Menu /></IconButton>
        <Typography>中大机械 APP</Typography>
      </Toolbar>
    </AppBar>
  );
}

function NewsCard(props) {
  return (
    <Card sx={{ backgroundColor: "#FAFAFA" }}>
      <CardMedia component="img" height="140" image="http://www.zdjxzz.cn/uploadfile/2018/11/20/1542694533713294.jpg" alt="green iguana" />
      <CardContent>
        <Typography gutterBottom variant="h5" component="div">公司动态</Typography>
        <Typography variant="body2" color="text.secondary">【品质工程】中大设备助建巴基斯坦PKM高速公路</Typography>
      </CardContent>
      <CardActions>
        <Button size="small" onClick={() => window.location.href="http://www.zdjxzz.cn/news/55.html"}>更多</Button>
      </CardActions>
    </Card>
  );
}

function ServiceCard(props) {
  const navigate = useNavigate();
  const onClick = useCallback(() => navigate("./device", { replace: true }), [navigate])
  return (
    <Card sx={{ backgroundColor: "#FAFAFA" }}>
      <CardHeader title="应用列表" />
      <Divider />
      <CardContent>
        <Grid container spacing={1}>
          <Grid item xs={3} style={{ textAlign: "center" }}>
            <Button onClick={onClick}>
              <DeviceHub fontSize="large" />
            </Button>
          </Grid>
          <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
          <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
          <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
          <Grid item xs={3} style={{ textAlign: "center" }}>
            <Typography variant="body1">设备监控</Typography>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  )
}

export default function Main() {
  return (
    <Box>
      <Header />
      <Container sx={{ pt: 9, pb: 2, px: 2 }}>
        <Stack spacing={1}>
          <NewsCard />
          <ServiceCard />
        </Stack>
      </Container>
    </Box>
  );
}
