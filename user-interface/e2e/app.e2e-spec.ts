import { UserInterfacePage } from './app.po';

describe('user-interface App', () => {
  let page: UserInterfacePage;

  beforeEach(() => {
    page = new UserInterfacePage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!');
  });
});
