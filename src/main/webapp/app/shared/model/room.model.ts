export interface IRoom {
  id?: number;
  code?: string;
  name?: string;
  location?: string | null;
  capacity?: number;
  active?: boolean;
}

export const defaultValue: Readonly<IRoom> = {
  active: false,
};
