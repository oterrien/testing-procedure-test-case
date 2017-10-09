export interface IPassword {
  value: string;
  encoded: boolean;
}

export interface IUser {
  id: number;
  login: string;
  password: IPassword;
  roles: string[];
}
