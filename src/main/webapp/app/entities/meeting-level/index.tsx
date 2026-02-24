import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MeetingLevel from './meeting-level';
import MeetingLevelDetail from './meeting-level-detail';
import MeetingLevelUpdate from './meeting-level-update';
import MeetingLevelDeleteDialog from './meeting-level-delete-dialog';

const MeetingLevelRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MeetingLevel />} />
    <Route path="new" element={<MeetingLevelUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingLevelDetail />} />
      <Route path="edit" element={<MeetingLevelUpdate />} />
      <Route path="delete" element={<MeetingLevelDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingLevelRoutes;
