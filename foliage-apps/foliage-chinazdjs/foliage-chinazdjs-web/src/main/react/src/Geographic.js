import React, { useCallback, useState, useEffect, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Map, Marker } from "react-bmapgl";
import { Fab } from "@mui/material";
import { ArrowBackIosNew } from "@mui/icons-material";
import { AxiosContext } from "./Context";

function BackButton() {
  const navigate = useNavigate();
  const onClick = useCallback(() => navigate("./device", { replace: true }), [navigate])
  return (
    <Fab sx={{ position: "fixed", left: 10, top: 10, zIndex: "tooltip" }} color="primary" size="small" onClick={onClick}>
      <ArrowBackIosNew />
    </Fab>
  );
}

export default function Geographic(props) {
  const axios = useContext(AxiosContext);
  const { id } = useParams();
  const [point, setPoint] = useState({ lng: 116.331398, lat: 39.897445 });
  useEffect(() => {
    axios.get(`/api/v1/geographic/${id}`)
      .then((response) => {
        const { longitude: lng, latitude: lat } = response.data.coordinate;
        setPoint({ lng, lat });
      });
  }, [axios, id]);
  return (
    <div style={{ height: "100vh" }}>
      <BackButton />
      <Map autoViewport style={{ height: "100vh" }} center={point} zoom={13}>
        <Marker position={point} />
      </Map>
    </div>
  );
}
