import * as React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import BaiduMap from "./BaiduMap";
import Main from "./Main";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/map" element={<BaiduMap />} />
      </Routes>
    </BrowserRouter>
  );
}
