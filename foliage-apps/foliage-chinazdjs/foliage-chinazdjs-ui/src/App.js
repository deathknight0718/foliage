import * as React from "react";
import * as axios from "axios";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Main from "./Main";
import Device from "./Device";
import Geographic from "./Geographic";
import Specification from "./Specification";
import { AxiosProvider } from "./Context";

const instance = axios.create({
  baseURL: `${process.env.REACT_APP_HOST}/foliage-chinazdjs-web`,
  withCredentials: true,
  auth: { username: "tomcat", password: "secret" }
});

export default function App() {
  return (
    <BrowserRouter>
      <AxiosProvider value={instance}>
          <Routes>
            <Route path="/" element={<Main />} />
            <Route path="/device" element={<Device />} />
            <Route path="/geographic/:id" element={<Geographic />} />
            <Route path="/specification/:id" element={<Specification />} />
          </Routes>
      </AxiosProvider>
    </BrowserRouter>
  );
}
