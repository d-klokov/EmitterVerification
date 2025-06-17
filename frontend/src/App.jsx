import "./App.css";
import { ThemeProvider } from "@mui/material/styles";
import theme from "./presentation/Theme";
import { BrowserRouter } from "react-router-dom";
import Navbar from "./presentation/components/Navbar/Navbar";
import AppRoutes from "./presentation/Routes";

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Navbar />

        <AppRoutes />
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
