import * as React from "react";
import * as axios from "axios";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Main from "./Main";
import Device from "./Device";
import Geographic from "./Geographic";
import Specification from "./Specification";

const instance = axios.create({ baseURL: "https://localhost:7443/foliage-chinazdjs-web" });

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/device" element={<Device axios={instance} />} />
        <Route path="/geographic" element={<Geographic />} />
        <Route path="/specification" element={<Specification />} />
      </Routes>
    </BrowserRouter>
  );
}
