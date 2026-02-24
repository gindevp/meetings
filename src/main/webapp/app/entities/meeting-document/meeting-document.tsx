import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, Translate, byteSize, getSortState, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './meeting-document.reducer';

export const MeetingDocument = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const meetingDocumentList = useAppSelector(state => state.meetingDocument.entities);
  const loading = useAppSelector(state => state.meetingDocument.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="meeting-document-heading" data-cy="MeetingDocumentHeading">
        <Translate contentKey="meetingsApp.meetingDocument.home.title">Meeting Documents</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="meetingsApp.meetingDocument.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/meeting-document/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="meetingsApp.meetingDocument.home.createLabel">Create new Meeting Document</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {meetingDocumentList && meetingDocumentList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="meetingsApp.meetingDocument.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('docType')}>
                  <Translate contentKey="meetingsApp.meetingDocument.docType">Doc Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('docType')} />
                </th>
                <th className="hand" onClick={sort('fileName')}>
                  <Translate contentKey="meetingsApp.meetingDocument.fileName">File Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileName')} />
                </th>
                <th className="hand" onClick={sort('contentType')}>
                  <Translate contentKey="meetingsApp.meetingDocument.contentType">Content Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentType')} />
                </th>
                <th className="hand" onClick={sort('file')}>
                  <Translate contentKey="meetingsApp.meetingDocument.file">File</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('file')} />
                </th>
                <th className="hand" onClick={sort('uploadedAt')}>
                  <Translate contentKey="meetingsApp.meetingDocument.uploadedAt">Uploaded At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uploadedAt')} />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingDocument.uploadedBy">Uploaded By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.meetingDocument.meeting">Meeting</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {meetingDocumentList.map((meetingDocument, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/meeting-document/${meetingDocument.id}`} color="link" size="sm">
                      {meetingDocument.id}
                    </Button>
                  </td>
                  <td>{meetingDocument.docType}</td>
                  <td>{meetingDocument.fileName}</td>
                  <td>{meetingDocument.contentType}</td>
                  <td>
                    {meetingDocument.file ? (
                      <div>
                        {meetingDocument.fileContentType ? (
                          <a onClick={openFile(meetingDocument.fileContentType, meetingDocument.file)}>
                            <Translate contentKey="entity.action.open">Open</Translate>
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {meetingDocument.fileContentType}, {byteSize(meetingDocument.file)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>
                    {meetingDocument.uploadedAt ? (
                      <TextFormat type="date" value={meetingDocument.uploadedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{meetingDocument.uploadedBy ? meetingDocument.uploadedBy.login : ''}</td>
                  <td>
                    {meetingDocument.meeting ? <Link to={`/meeting/${meetingDocument.meeting.id}`}>{meetingDocument.meeting.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/meeting-document/${meetingDocument.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/meeting-document/${meetingDocument.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/meeting-document/${meetingDocument.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="meetingsApp.meetingDocument.home.notFound">No Meeting Documents found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default MeetingDocument;
