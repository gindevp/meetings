import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedBlobField, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getMeetings } from 'app/entities/meeting/meeting.reducer';
import { createEntity, getEntity, reset, updateEntity } from './meeting-document.reducer';

export const MeetingDocumentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const meetings = useAppSelector(state => state.meeting.entities);
  const meetingDocumentEntity = useAppSelector(state => state.meetingDocument.entity);
  const loading = useAppSelector(state => state.meetingDocument.loading);
  const updating = useAppSelector(state => state.meetingDocument.updating);
  const updateSuccess = useAppSelector(state => state.meetingDocument.updateSuccess);

  const handleClose = () => {
    navigate('/meeting-document');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getMeetings({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.uploadedAt = convertDateTimeToServer(values.uploadedAt);

    const entity = {
      ...meetingDocumentEntity,
      ...values,
      uploadedBy: users.find(it => it.id.toString() === values.uploadedBy?.toString()),
      meeting: meetings.find(it => it.id.toString() === values.meeting?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          uploadedAt: displayDefaultDateTime(),
        }
      : {
          ...meetingDocumentEntity,
          uploadedAt: convertDateTimeFromServer(meetingDocumentEntity.uploadedAt),
          uploadedBy: meetingDocumentEntity?.uploadedBy?.id,
          meeting: meetingDocumentEntity?.meeting?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="meetingsApp.meetingDocument.home.createOrEditLabel" data-cy="MeetingDocumentCreateUpdateHeading">
            <Translate contentKey="meetingsApp.meetingDocument.home.createOrEditLabel">Create or edit a MeetingDocument</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="meeting-document-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('meetingsApp.meetingDocument.docType')}
                id="meeting-document-docType"
                name="docType"
                data-cy="docType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meetingDocument.fileName')}
                id="meeting-document-fileName"
                name="fileName"
                data-cy="fileName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meetingDocument.contentType')}
                id="meeting-document-contentType"
                name="contentType"
                data-cy="contentType"
                type="text"
              />
              <ValidatedBlobField
                label={translate('meetingsApp.meetingDocument.file')}
                id="meeting-document-file"
                name="file"
                data-cy="file"
                openActionLabel={translate('entity.action.open')}
              />
              <ValidatedField
                label={translate('meetingsApp.meetingDocument.uploadedAt')}
                id="meeting-document-uploadedAt"
                name="uploadedAt"
                data-cy="uploadedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="meeting-document-uploadedBy"
                name="uploadedBy"
                data-cy="uploadedBy"
                label={translate('meetingsApp.meetingDocument.uploadedBy')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="meeting-document-meeting"
                name="meeting"
                data-cy="meeting"
                label={translate('meetingsApp.meetingDocument.meeting')}
                type="select"
                required
              >
                <option value="" key="0" />
                {meetings
                  ? meetings.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/meeting-document" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default MeetingDocumentUpdate;
