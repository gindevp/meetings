import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RoomEquipment from './room-equipment';
import RoomEquipmentDetail from './room-equipment-detail';
import RoomEquipmentUpdate from './room-equipment-update';
import RoomEquipmentDeleteDialog from './room-equipment-delete-dialog';

const RoomEquipmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RoomEquipment />} />
    <Route path="new" element={<RoomEquipmentUpdate />} />
    <Route path=":id">
      <Route index element={<RoomEquipmentDetail />} />
      <Route path="edit" element={<RoomEquipmentUpdate />} />
      <Route path="delete" element={<RoomEquipmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RoomEquipmentRoutes;
