import { Route, Routes } from "react-router-dom";
import EmitterTypesList from "./components/emittertype/EmitterTypesList";
import AddEmitterType from "./components/emittertype/CreateEmitterType";
import EditEmitterType from "./components/emittertype/EditEmitterType";
import DeleteEmitterType from "./components/emittertype/DeleteEmitterType";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/types-list" element={<EmitterTypesList />} />
      <Route path="/create-type" element={<AddEmitterType />} />
      <Route path="/edit-type/:id" element={<EditEmitterType />} />
      <Route path="/delete-type/:id" element={<DeleteEmitterType />} />
    </Routes>
  );
};

export default AppRoutes;
