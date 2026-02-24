import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MeetingType from './meeting-type';
import MeetingTypeDetail from './meeting-type-detail';
import MeetingTypeUpdate from './meeting-type-update';
import MeetingTypeDeleteDialog from './meeting-type-delete-dialog';

const MeetingTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MeetingType />} />
    <Route path="new" element={<MeetingTypeUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingTypeDetail />} />
      <Route path="edit" element={<MeetingTypeUpdate />} />
      <Route path="delete" element={<MeetingTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingTypeRoutes;
