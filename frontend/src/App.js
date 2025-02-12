import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import EmitterTypesList from './components/emittertype/EmitterTypesList';
import Navbar from './components/navbar/Navbar';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import HomePage from './components/HomePage';
import CreateEmitterType from './components/emittertype/CreateEmitterType';
import EditEmitterType from './components/emittertype/EditEmitterType';
import DeleteEmitterType from './components/emittertype/DeleteEmitterType';

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
          <Route path='/create-type' element={<CreateEmitterType />} />
          <Route path='/edit-type/:id' element={<EditEmitterType />} />
          <Route path='/delete-type/:id' element={<DeleteEmitterType />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
