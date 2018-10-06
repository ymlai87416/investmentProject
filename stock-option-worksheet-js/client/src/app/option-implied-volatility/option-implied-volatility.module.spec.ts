import { OptionImpliedVolatilityModule } from './option-implied-volatility.module';

describe('OptionImpliedVolatilityModule', () => {
  let optionImpliedVolatilityModule: OptionImpliedVolatilityModule;

  beforeEach(() => {
    optionImpliedVolatilityModule = new OptionImpliedVolatilityModule();
  });

  it('should create an instance', () => {
    expect(optionImpliedVolatilityModule).toBeTruthy();
  });
});
