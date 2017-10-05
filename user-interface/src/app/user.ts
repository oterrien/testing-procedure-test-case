export class Password {
  value: string;
  encoded: boolean;
}

export class User {
  id: number;
  login: string;
  password: Password;
  roles: Array<string>;
}
