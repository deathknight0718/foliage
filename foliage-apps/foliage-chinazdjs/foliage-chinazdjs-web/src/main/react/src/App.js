import * as React from "react";
import * as axios from "axios";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Main from "./Main";
import Device from "./Device";
import Geographic from "./Geographic";
import Specification from "./Specification";
import { AxiosProvider } from "./Context";

const { REACT_APP_REST, REACT_APP_NAME, NODE_ENV } = process.env;
const options = { baseURL: REACT_APP_REST };
if (NODE_ENV === "development") {
  options.auth = { username: "tomcat", password: "secret" };
}
const instance = axios.create(options);

export default function App() {
  return (
    <BrowserRouter>
      <AxiosProvider value={instance}>
          <Routes>
            <Route path={`/${REACT_APP_NAME}/`} element={<Main />} />
            <Route path={`/${REACT_APP_NAME}/device`} element={<Device />} />
            <Route path={`/${REACT_APP_NAME}/geographic/:id`} element={<Geographic />} />
            <Route path={`/${REACT_APP_NAME}/specification/:id`} element={<Specification />} />
          </Routes>
      </AxiosProvider>
    </BrowserRouter>
  );
}
