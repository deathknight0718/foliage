import React from "react";
import { useNavigate } from "react-router-dom";
import { Box, Container, Grid } from "@mui/material";
import { Card, CardContent, CardMedia } from "@mui/material";
import { Stack, Typography } from "@mui/material";
import { IconButton, Button } from "@mui/material";
import { AppBar, Toolbar } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { Menu, DeviceHub } from "@mui/icons-material";
import Carousel from "react-material-ui-carousel";

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

function ProductPaper(props) {
  const { title, image, href } = props;
  const { palette } = useTheme();
  const onClick = () => { window.location.href = href; }
  return (
    <Card sx={{ backgroundColor: palette.grey[50] }}>
      <CardMedia component="img" height="176" image={image} />
      <CardContent onClick={onClick}>
        <Typography variant="body2" color="text.secondary">{title}</Typography>
      </CardContent>
    </Card>
  );
}

function ProductCarousel(props) {
  const site = "http://www.zdjxzz.cn";
  return (
    <Carousel>
      <ProductPaper title="中大设备助建巴基斯坦PKM高速公路" href={`${site}/news/55.html`} image={`${site}/uploadfile/2018/11/20/1542694533713294.jpg`} />
      <ProductPaper title="传承匠心成就中国公路摊铺专家" href={`${site}/news/54.html`} image={`${site}/uploadfile/61821542694054.png`} />
      <ProductPaper title="湖南中大机械助力成都天府国际机场" href={`${site}/news/video-detail-34.html`} image={`${site}/uploadfile/2018/01/22/1516606274413707.jpg`} />
    </Carousel>
  );
}

function ServiceCard(props) {
  const navigate = useNavigate();
  return (
    <Grid container spacing={1}>
      <Grid item xs={3} style={{ textAlign: "center" }}>
        <Button onClick={() => navigate("./device")}>
          <DeviceHub fontSize="large" />
        </Button>
      </Grid>
      <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
      <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
      <Grid item xs={3} style={{ textAlign: "center" }}></Grid>
      <Grid item xs={3} style={{ textAlign: "center" }}>
        <Typography variant="body2">设备监控</Typography>
      </Grid>
    </Grid>
  )
}

export default function Main() {
  return (
    <Box>
      <Header />
      <Container sx={{ pt: 9, pb: 2, px: 2 }}>
        <Stack spacing={1}>
          <ProductCarousel />
          <ServiceCard />
        </Stack>
      </Container>
    </Box>
  );
}
