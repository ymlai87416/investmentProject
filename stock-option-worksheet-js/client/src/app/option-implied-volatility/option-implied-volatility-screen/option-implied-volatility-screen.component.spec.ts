import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OptionImpliedVolatilityScreenComponent } from './option-implied-volatility-screen.component';

describe('OptionImpliedVolatilityScreenComponent', () => {
  let component: OptionImpliedVolatilityScreenComponent;
  let fixture: ComponentFixture<OptionImpliedVolatilityScreenComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OptionImpliedVolatilityScreenComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OptionImpliedVolatilityScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
