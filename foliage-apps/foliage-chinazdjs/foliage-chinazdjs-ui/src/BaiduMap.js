import * as React from "react";
import * as axios from "axios";
import { Map, Marker, ZoomControl } from "react-bmapgl";
import { Fab } from "@mui/material";
import { ArrowBackIosNew } from "@mui/icons-material";
import { useSearchParams } from "react-router-dom";

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
        <Fab sx={{ position: "fixed", left: 10, top: 10, zIndex: "tooltip" }} color="primary" size="small">
          <ArrowBackIosNew />
        </Fab>
        <Map autoViewport style={{ height: "100vh" }} center={position} zoom={13}>
          <Marker position={position} />
          <ZoomControl />
        </Map>
      </div>
    );
  }
}

export default function BaiduMap(props) {
  const [searchParams] = useSearchParams();
  const devcode = searchParams.get("devcode");
  return <InternalComponent {...props} devcode={devcode} />;
}
