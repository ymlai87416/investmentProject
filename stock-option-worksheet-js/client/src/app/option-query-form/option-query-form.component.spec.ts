import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OptionQueryFormComponent } from './option-query-form.component';

describe('OptionQueryFormComponent', () => {
  let component: OptionQueryFormComponent;
  let fixture: ComponentFixture<OptionQueryFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OptionQueryFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OptionQueryFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
