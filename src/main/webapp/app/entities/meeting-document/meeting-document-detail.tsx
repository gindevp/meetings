import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-document.reducer';

export const MeetingDocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingDocumentEntity = useAppSelector(state => state.meetingDocument.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingDocumentDetailsHeading">
          <Translate contentKey="meetingsApp.meetingDocument.detail.title">MeetingDocument</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{meetingDocumentEntity.id}</dd>
          <dt>
            <span id="docType">
              <Translate contentKey="meetingsApp.meetingDocument.docType">Doc Type</Translate>
            </span>
          </dt>
          <dd>{meetingDocumentEntity.docType}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="meetingsApp.meetingDocument.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{meetingDocumentEntity.fileName}</dd>
          <dt>
            <span id="contentType">
              <Translate contentKey="meetingsApp.meetingDocument.contentType">Content Type</Translate>
            </span>
          </dt>
          <dd>{meetingDocumentEntity.contentType}</dd>
          <dt>
            <span id="file">
              <Translate contentKey="meetingsApp.meetingDocument.file">File</Translate>
            </span>
          </dt>
          <dd>
            {meetingDocumentEntity.file ? (
              <div>
                {meetingDocumentEntity.fileContentType ? (
                  <a onClick={openFile(meetingDocumentEntity.fileContentType, meetingDocumentEntity.file)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {meetingDocumentEntity.fileContentType}, {byteSize(meetingDocumentEntity.file)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="uploadedAt">
              <Translate contentKey="meetingsApp.meetingDocument.uploadedAt">Uploaded At</Translate>
            </span>
          </dt>
          <dd>
            {meetingDocumentEntity.uploadedAt ? (
              <TextFormat value={meetingDocumentEntity.uploadedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingDocument.uploadedBy">Uploaded By</Translate>
          </dt>
          <dd>{meetingDocumentEntity.uploadedBy ? meetingDocumentEntity.uploadedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="meetingsApp.meetingDocument.meeting">Meeting</Translate>
          </dt>
          <dd>{meetingDocumentEntity.meeting ? meetingDocumentEntity.meeting.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/meeting-document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-document/${meetingDocumentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingDocumentDetail;
