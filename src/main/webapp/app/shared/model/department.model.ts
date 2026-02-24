export interface IDepartment {
  id?: number;
  code?: string;
  name?: string;
  description?: string | null;
}

export const defaultValue: Readonly<IDepartment> = {};
