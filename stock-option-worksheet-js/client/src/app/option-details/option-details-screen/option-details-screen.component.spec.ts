import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OptionDetailsScreenComponent } from './option-details-screen.component';

describe('OptionDetailsScreenComponent', () => {
  let component: OptionDetailsScreenComponent;
  let fixture: ComponentFixture<OptionDetailsScreenComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OptionDetailsScreenComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OptionDetailsScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
