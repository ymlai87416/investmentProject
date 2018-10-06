import { OptionDetailsModule } from './option-details.module';

describe('OptionDetailsModule', () => {
  let optionDetailsModule: OptionDetailsModule;

  beforeEach(() => {
    optionDetailsModule = new OptionDetailsModule();
  });

  it('should create an instance', () => {
    expect(optionDetailsModule).toBeTruthy();
  });
});
