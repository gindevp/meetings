import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Equipment from './equipment';
import EquipmentDetail from './equipment-detail';
import EquipmentUpdate from './equipment-update';
import EquipmentDeleteDialog from './equipment-delete-dialog';

const EquipmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Equipment />} />
    <Route path="new" element={<EquipmentUpdate />} />
    <Route path=":id">
      <Route index element={<EquipmentDetail />} />
      <Route path="edit" element={<EquipmentUpdate />} />
      <Route path="delete" element={<EquipmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EquipmentRoutes;
