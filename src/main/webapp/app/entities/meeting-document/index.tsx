import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MeetingDocument from './meeting-document';
import MeetingDocumentDetail from './meeting-document-detail';
import MeetingDocumentUpdate from './meeting-document-update';
import MeetingDocumentDeleteDialog from './meeting-document-delete-dialog';

const MeetingDocumentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MeetingDocument />} />
    <Route path="new" element={<MeetingDocumentUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingDocumentDetail />} />
      <Route path="edit" element={<MeetingDocumentUpdate />} />
      <Route path="delete" element={<MeetingDocumentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingDocumentRoutes;
