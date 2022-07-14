import React, { useState, useEffect, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Map, Marker } from "react-bmapgl";
import { Fab, Drawer, IconButton, Box } from "@mui/material";
import { List, ListItem, ListItemText, ListItemButton, ListSubheader } from "@mui/material";
import { ArrowBackIosNew, Comment } from "@mui/icons-material";
import { AxiosContext } from "./Context";

const { REACT_APP_NAME } = process.env;

function BackButton() {
  const navigate = useNavigate();
  return (
    <Fab sx={{ position: "fixed", left: 10, top: 10, zIndex: "tooltip" }} color="primary" size="small" onClick={() => navigate(`/${REACT_APP_NAME}/device`)}>
      <ArrowBackIosNew />
    </Fab>
  );
}

function onGeographic(id, navigate, onChange) {
  navigate(`/${REACT_APP_NAME}/geographic/${id}`, { relace: true });
  onChange();
}

function onSpecification(id, navigate) {
  navigate(`/${REACT_APP_NAME}/specification/${id}`, { relace: true });
}

function DeviceDrawer(props) {
  const navigate = useNavigate();
  const { id } = useParams();
  const { data, open, onClose } = props;
  const paperProps = { sx: { borderTopLeftRadius: 8, borderTopRightRadius: 8 } };
  return (
    <Drawer PaperProps={paperProps} open={open} anchor="bottom" onClose={onClose}>
      <Box sx={{ height: 196, overflow: "auto" }}>
        <List dense subheader={<ListSubheader>{data[0]["province"]}</ListSubheader>}>
          {data.map((item, index) => (
            <ListItem key={`list-item-${index}`} secondaryAction={<IconButton edge="end" onClick={() => onSpecification(item.device.id, navigate)}><Comment color="primary" /></IconButton>}>
              <ListItemButton dense selected={id === item.device.id} autoFocus={id === item.device.id} onClick={() => onGeographic(item.device.id, navigate, onClose)}>
                <ListItemText primary={`${item.device.devcode} - ${item.device.name}`} secondary={item.address} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Box>
    </Drawer>
  );
}

function DeviceMarker(props) {
  const { id } = useParams();
  const navigate = useNavigate();
  const onClick = () => {
    if (id === device.id) return onDrawing(true);
    navigate(`/${REACT_APP_NAME}/geographic/${device.id}`, { relace: true });
    onDrawing(false);
  };
  const { device, coordinate: { longitude: lng, latitude: lat }, onDrawing } = props;
  return <Marker icon={device.id === id ? "loc_blue" : "simple_blue"} position={{ lng, lat }} onClick={onClick} />
}

export default function Geographic(props) {
  const axios = useContext(AxiosContext);
  const { id } = useParams();
  const [devices, setDevices] = useState(null);
  const [devicePoint, setDevicePoint] = useState({ lng: 116.331398, lat: 39.897445 });
  const [drawing, setDrawing] = useState(false);
  useEffect(() => {
    axios.get(`/api/v1/geographic/${id}`)
      .then((response) => {
        const { longitude: lng, latitude: lat } = response.data.current.coordinate;
        setDevicePoint({ lng, lat });
        setDevices([...response.data.geographicsByProvince]);
      })
  }, [axios, id]);
  return (
    <div style={{ height: "100vh" }}>
      <BackButton />
      {!!devices && <DeviceDrawer data={devices} open={drawing} onClose={() => setDrawing(false)} />}
      <Map autoViewport style={{ height: "100vh" }} center={devicePoint} zoom={13}>
        {!!devices && devices.map((item, index) => <DeviceMarker key={`device-marker-${index}`} device={item.device} coordinate={item.coordinate} onDrawing={setDrawing} />)}
      </Map>
    </div>
  );
}
