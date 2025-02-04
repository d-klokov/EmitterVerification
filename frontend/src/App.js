import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import EmitterTypesList from './components/emittertype/EmitterTypesList';
import Navbar from './components/navbar/Navbar';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import HomePage from './components/HomePage';

const theme = createTheme({
  palette: {
    primary: {
      light: '#b2dfdb',
      main: '#00897b',
      dark: '#00695c'
    }
  },
})

function App() {
  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>

        <Navbar />

        <Routes>
          <Route path='/' element={<HomePage />} />
          <Route path='/types-list' element={<EmitterTypesList />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
