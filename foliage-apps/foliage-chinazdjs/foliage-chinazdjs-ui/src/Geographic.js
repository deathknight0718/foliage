import * as axios from "axios";
import React, { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Map, Marker } from "react-bmapgl";
import { Fab } from "@mui/material";
import { ArrowBackIosNew } from "@mui/icons-material";
import { useSearchParams } from "react-router-dom";

function BackButton() {
  const navigate = useNavigate();
  const onClick = useCallback(() => navigate("/", { replace: true }), [navigate])
  return (
    <Fab sx={{ position: "fixed", left: 10, top: 10, zIndex: "tooltip" }} color="primary" size="small" onClick={onClick}>
      <ArrowBackIosNew />
    </Fab>
  );
}

class InternalComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      position: { lng: 116.331398, lat: 39.897445 }
    };
  }

  componentDidMount() {
    const { devcode } = this.props;
    const data = { devcodes: `${devcode}` };
    axios.post(`http://iot.scientop.com:7050/api/DeviceData/QueryRealGpsData`, data).then((res) => {
      const item = res.data.find((i) => i !== undefined);
      if (!item) return;
      const { Lng: lng, Lat: lat } = item;
      this.setState({ position: { lng, lat } });
    });
  }

  render() {
    const { position } = this.state;
    return (
      <div style={{ height: "100vh" }}>
        <BackButton />
        <Map autoViewport style={{ height: "100vh" }} center={position} zoom={13}>
          <Marker position={position} />
        </Map>
      </div>
    );
  }
}

export default function Geographic(props) {
  const [searchParams] = useSearchParams();
  const devcode = searchParams.get("devcode");
  return <InternalComponent {...props} devcode={devcode} />;
}
