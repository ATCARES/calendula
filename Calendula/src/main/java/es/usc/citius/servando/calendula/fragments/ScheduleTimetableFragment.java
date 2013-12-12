package es.usc.citius.servando.calendula.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import es.usc.citius.servando.calendula.R;
import es.usc.citius.servando.calendula.model.Routine;
import es.usc.citius.servando.calendula.store.RoutineStore;
import es.usc.citius.servando.calendula.util.ScheduleCreationStateHolder;


/**
 * Created by joseangel.pineiro on 12/11/13.
 */
public class ScheduleTimetableFragment extends Fragment {

    LinearLayout timetableContainer;

    Integer doses[] = new Integer[]{1, 2, 3, 4, 5, 6};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_timetable, container, false);
        timetableContainer = (LinearLayout) rootView.findViewById(R.id.schedule_timetable_container);
        setupScheduleSpinner(rootView);
        setupDaySelectionListeners(rootView);
        return rootView;
    }

    private void setupScheduleSpinner(View rootView) {
        Spinner spinner = (Spinner) rootView.findViewById(R.id.schedules_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.schedules_array, R.layout.spinner_text_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                Log.d(getTag(), "Selected: " + selected);
                onScheduleSelected(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
    }

    void onScheduleSelected(String selection) {

        int timesPerDay = 1;
        String schedules[] = getResources().getStringArray(R.array.schedules_array);

        // obtain times per day from selected schedule
        for (int i = 0; i < schedules.length; i++) {
            if (schedules[i].equalsIgnoreCase(selection)) {
                timesPerDay = i + 1;
                break;
            }
        }

        List<Routine> routines = RoutineStore.getInstance().asList();
        addTimetableEntries(timesPerDay, routines);
    }


    void setupDaySelectionListeners(View rootView) {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView text = ((TextView) view);


                if (text.getTag() == null || ((Boolean) text.getTag()) == true) {
                    text.setTag(false);
                    ((TextView) view).setTextAppearance(getActivity(), R.style.schedule_day_unselected);
                } else {
                    text.setTag(true);
                    ((TextView) view).setTextAppearance(getActivity(), R.style.schedule_day_selected);
                }

                switch (text.getId()) {
                    case R.id.day_mo:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(0);
                        break;
                    case R.id.day_tu:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(1);
                        break;
                    case R.id.day_we:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(2);
                        break;
                    case R.id.day_th:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(3);
                        break;
                    case R.id.day_fr:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(4);
                        break;
                    case R.id.day_sa:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(5);
                        break;
                    case R.id.day_su:
                        ScheduleCreationStateHolder.getInstance().toggleSelectedDay(6);
                        break;
                    default:
                        break;
                }
            }
        };

        rootView.findViewById(R.id.day_mo).setOnClickListener(listener);
        rootView.findViewById(R.id.day_tu).setOnClickListener(listener);
        rootView.findViewById(R.id.day_we).setOnClickListener(listener);
        rootView.findViewById(R.id.day_th).setOnClickListener(listener);
        rootView.findViewById(R.id.day_fr).setOnClickListener(listener);
        rootView.findViewById(R.id.day_sa).setOnClickListener(listener);
        rootView.findViewById(R.id.day_su).setOnClickListener(listener);


    }


    void addTimetableEntries(int timesPerDay, List<Routine> routines) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        String[] routineNames = getUpdatedRoutineNames();
        timetableContainer.removeAllViews();
        for (int i = 0; i < timesPerDay; i++) {
            Routine r = (i < routines.size()) ? routines.get(i) : null;
            Log.d(getTag(), "Routine " + i + " is" + (r == null ? " null" : " not null"));
            View view = buildTimetableEntry(r, routineNames);
            timetableContainer.addView(view, params);
        }
    }

    void orderTimetableEntries() {
        //TODO: Order entries if needed
    }

    String[] getUpdatedRoutineNames() {
        String[] routinesFromStore = RoutineStore.getInstance().routineNames();
        String[] routineNames = new String[routinesFromStore.length + 1];

        for (int i = 0; i < routinesFromStore.length; i++) {
            routineNames[i] = routinesFromStore[i];
        }

        routineNames[routineNames.length - 1] = getString(R.string.create_new_routine);
        return routineNames;
    }


    View buildTimetableEntry(Routine r, String[] routineNames) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View entry = inflater.inflate(R.layout.schedule_timetable_entry, null);
        updateEntryTime(r, entry);
        setupScheduleEntrySpinners(entry, r, routineNames);
        return entry;
    }

    void updateRoutineSelectionAdapter(final View entryView, Spinner routineSpinner, String[] routineNames) {
        ArrayAdapter<String> routineAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, routineNames);
        routineAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        routineSpinner.setAdapter(routineAdapter);

//        routineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selected = (String)adapterView.getItemAtPosition(i);
//
//                Log.d("SELECTED", "" + selected + ", " + getString(R.string.create_new_routine));
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }


    private void setupScheduleEntrySpinners(final View entryView, Routine r, String[] routineNames) {
        final Spinner routineSpinner = (Spinner) entryView.findViewById(R.id.entry_routine_spinner);
        final Spinner doseSpinner = (Spinner) entryView.findViewById(R.id.entry_dose_spinner);

        // set up the routine selection adapter
        updateRoutineSelectionAdapter(entryView, routineSpinner, routineNames);

        if (r != null) {
            routineSpinner.setSelection(Arrays.asList(routineNames).indexOf(r.getName()));
        } else {
            routineSpinner.setSelection(routineNames.length - 1);
        }

        // set up the dose selection adapter
        ArrayAdapter<Integer> doseAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, doses);
        doseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        doseSpinner.setAdapter(doseAdapter);
        // select 1 pill by default
        doseSpinner.setSelection(0);

        // setup listeners
        routineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                Routine r = RoutineStore.getInstance().getRoutineByName(selected);
                if (r != null) {
                    updateEntryTime(r, entryView);
                } else {
                    updateEntryTime(null, entryView);
                    showAddNewRoutineDialog(entryView);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        routineSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (((String) routineSpinner.getSelectedItem()).equalsIgnoreCase(getString(R.string.create_new_routine))) {
                        showAddNewRoutineDialog(entryView);
                        return true;
                    }
                }
                return false;
            }
        });


        doseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Integer selected = (Integer) adapterView.getItemAtPosition(i);
                Log.d(getTag(), "Selected: " + selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    void updateEntryTime(Routine r, View entry) {
        String hourText;
        String minuteText;
        if (r != null) {
            hourText = (r.getTime().getHourOfDay() < 10 ? "0" + r.getTime().getHourOfDay() : r.getTime().getHourOfDay()) + ":";
            minuteText = (r.getTime().getMinuteOfHour() < 10 ? "0" + r.getTime().getMinuteOfHour() : r.getTime().getMinuteOfHour()) + "";
        } else {
            hourText = "--:";
            minuteText = "--";
        }

        ((TextView) entry.findViewById(R.id.hour_text)).setText(hourText);
        ((TextView) entry.findViewById(R.id.minute_text)).setText(minuteText);
    }


    void showAddNewRoutineDialog(final View entryView) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        final RoutineCreateOrEditFragment addRoutineFragment = new RoutineCreateOrEditFragment();
        addRoutineFragment.setOnRoutineEditListener(new RoutineCreateOrEditFragment.OnRoutineEditListener() {
            @Override
            public void onRoutineEdited(Routine r) {
                // do nothing
            }

            @Override
            public void onRoutineCreated(final Routine r) {
                Spinner rSpinner = (Spinner) entryView.findViewById(R.id.entry_routine_spinner);
                String names[] = getUpdatedRoutineNames();
                updateRoutineSelectionAdapter(entryView, rSpinner, names);

                Log.d(getTag(), "Routine name: " + r.getName());
                Log.d(getTag(), "Routine time: " + r.getTimeAsString());
                Log.d(getTag(), "Names: " + Arrays.toString(names));

                int selection = Arrays.asList(names).indexOf(r.getName());
                rSpinner.setSelection(selection);

                updateEntryTime(r, entryView);
                addRoutineFragment.dismiss();
            }
        });

        addRoutineFragment.show(fm, "fragment_edit_name");
    }
}