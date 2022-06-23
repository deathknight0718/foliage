import * as React from "react";
import * as axios from "axios";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Main from "./Main";
import Device from "./Device";
import Geographic from "./Geographic";
import Specification from "./Specification";
import { AxiosProvider } from "./Context";

const instance = axios.create({ baseURL: `/${process.env.REACT_APP_NAME}` });

export default function App() {
  return (
    <BrowserRouter>
      <AxiosProvider value={instance}>
          <Routes>
            <Route path={`/${process.env.REACT_APP_NAME}/`} element={<Main />} />
            <Route path={`/${process.env.REACT_APP_NAME}/device`} element={<Device />} />
            <Route path={`/${process.env.REACT_APP_NAME}/geographic/:id`} element={<Geographic />} />
            <Route path={`/${process.env.REACT_APP_NAME}/specification/:id`} element={<Specification />} />
          </Routes>
      </AxiosProvider>
    </BrowserRouter>
  );
}
