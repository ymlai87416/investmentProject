import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpliedVolatilityGraphComponent } from './implied-volatility-graph.component';

describe('ImpliedVolatilityGraphComponent', () => {
  let component: ImpliedVolatilityGraphComponent;
  let fixture: ComponentFixture<ImpliedVolatilityGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImpliedVolatilityGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImpliedVolatilityGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
