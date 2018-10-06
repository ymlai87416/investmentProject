import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpliedVolatilitySummaryComponent } from './implied-volatility-summary.component';

describe('ImpliedVolatilitySummaryComponent', () => {
  let component: ImpliedVolatilitySummaryComponent;
  let fixture: ComponentFixture<ImpliedVolatilitySummaryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImpliedVolatilitySummaryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpliedVolatilitySummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
