import * as React from "react";
import axios from "axios";
import { createRoot } from "react-dom/client";
import { ThemeProvider } from "@mui/material/styles";
import { CssBaseline } from "@mui/material";
import App from "./App";
import theme from "./theme";

const rootElement = document.getElementById("root");
const root = createRoot(rootElement);

// axios.defaults.headers.post["Authorization"] = "Basic dG9tY2F0OnNlY3JldA==";

root.render(
  <ThemeProvider theme={theme}>
    <CssBaseline />
    <App />
  </ThemeProvider>
);
