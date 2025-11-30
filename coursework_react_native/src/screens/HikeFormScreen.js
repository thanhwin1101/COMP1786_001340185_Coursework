// src/screens/HikeFormScreen.js
import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  StyleSheet,
  Switch,
  TouchableOpacity,
  Alert,
  ScrollView,
  Platform,
} from 'react-native';
import DateTimePicker from '@react-native-community/datetimepicker';
import { Picker } from '@react-native-picker/picker';

import {
  insertHike,
  updateHike,
  getHikeById,
} from '../storage/db';

const PURPLE = '#6C47FF';

export default function HikeFormScreen({ route, navigation }) {
  const mode = route.params?.mode ?? 'add';
  const editingId = route.params?.id ?? null;

  const [name, setName] = useState('');
  const [location, setLocation] = useState('');
  const [date, setDate] = useState('');
  const [distance, setDistance] = useState('');
  const [duration, setDuration] = useState('');
  const [elevation, setElevation] = useState('');
  const [groupSize, setGroupSize] = useState('');
  const [terrain, setTerrain] = useState('');
  const [difficulty, setDifficulty] = useState('Moderate');
  const [hasParking, setHasParking] = useState(false);
  const [description, setDescription] = useState('');

  // state cho DatePicker
  const [showDatePicker, setShowDatePicker] = useState(false);

  useEffect(() => {
    if (mode === 'edit' && editingId) {
      loadExisting();
    }
  }, [mode, editingId]);

  async function loadExisting() {
    const h = await getHikeById(editingId);
    if (!h) return;
    setName(h.name);
    setLocation(h.location);
    setDate(h.date);
    setDistance(String(h.distance));
    setDuration(String(h.duration));
    setElevation(String(h.elevation));
    setGroupSize(String(h.groupSize));
    setTerrain(h.terrain ?? '');
    setDescription(h.description ?? '');
    setDifficulty(h.difficulty);
    setHasParking(!!h.hasParking);
  }

  function validate() {
    if (
      !name.trim() ||
      !location.trim() ||
      !date.trim() ||
      !distance.trim() ||
      !duration.trim() ||
      !elevation.trim() ||
      !groupSize.trim()
    ) {
      Alert.alert('Validation', 'Please fill all required fields (*)');
      return false;
    }
    if (
      isNaN(Number(distance)) ||
      isNaN(Number(duration)) ||
      isNaN(Number(elevation)) ||
      isNaN(Number(groupSize))
    ) {
      Alert.alert(
        'Validation',
        'Distance, duration, elevation and group size must be numbers'
      );
      return false;
    }
    return true;
  }

  function confirmSave() {
    if (!validate()) return;

    const summary =
      `Name: ${name}\n` +
      `Location: ${location}\n` +
      `Date: ${date}\n` +
      `Distance: ${distance} km\n` +
      `Duration: ${duration} h\n` +
      `Elevation: ${elevation} m\n` +
      `Difficulty: ${difficulty}\n` +
      `Parking: ${hasParking ? 'Yes' : 'No'}\n` +
      `Group size: ${groupSize}\n` +
      `Terrain: ${terrain}`;

    Alert.alert(
      mode === 'edit' ? 'Confirm changes' : 'Confirm new hike',
      summary,
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Confirm', onPress: saveToDb },
      ]
    );
  }

  async function saveToDb() {
    const hikeObj = {
      id: editingId,
      name: name.trim(),
      location: location.trim(),
      date: date.trim(),
      hasParking,
      distance: Number(distance),
      duration: Number(duration),
      elevation: Number(elevation),
      difficulty,
      groupSize: Number(groupSize),
      terrain: terrain.trim(),
      description: description.trim(),
    };

    if (mode === 'add') {
      await insertHike(hikeObj);
    } else {
      await updateHike(hikeObj);
    }

    navigation.goBack();
  }

  // xử lý khi chọn ngày
  function onChangeDate(event, selectedDate) {
    if (event.type === 'dismissed') {
      setShowDatePicker(false);
      return;
    }
    const currentDate = selectedDate || new Date();
    setShowDatePicker(false);

    const mm = String(currentDate.getMonth() + 1).padStart(2, '0');
    const dd = String(currentDate.getDate()).padStart(2, '0');
    const yyyy = currentDate.getFullYear();
    setDate(`${mm}/${dd}/${yyyy}`);
  }

  return (
    <ScrollView style={styles.scroll}>
      <View style={styles.container}>
        <Text style={styles.formTitle}>
          {mode === 'edit' ? 'Edit Hike' : 'Add New Hike'}
        </Text>

        <LabeledInput
          label="Hike Name *"
          value={name}
          onChangeText={setName}
        />
        <LabeledInput
          label="Location *"
          value={location}
          onChangeText={setLocation}
        />
        <LabeledInput
          label="Group size *"
          value={groupSize}
          onChangeText={setGroupSize}
          keyboardType="numeric"
        />
        <LabeledInput
          label="Terrain (optional)"
          value={terrain}
          onChangeText={setTerrain}
        />

        <View style={styles.row}>
          <LabeledInput
            style={styles.half}
            label="Distance (km) *"
            value={distance}
            onChangeText={setDistance}
            keyboardType="numeric"
          />
          <LabeledInput
            style={styles.half}
            label="Duration (hours) *"
            value={duration}
            onChangeText={setDuration}
            keyboardType="numeric"
          />
        </View>

        <View style={styles.row}>
          <LabeledInput
            style={styles.half}
            label="Elevation (m) *"
            value={elevation}
            onChangeText={setElevation}
            keyboardType="numeric"
          />

          {/* Difficulty: dropdown 3 lựa chọn */}
          <View style={[styles.field, styles.half]}>
            <Text style={styles.label}>Difficulty *</Text>
            <View style={styles.pickerWrapper}>
              <Picker
                selectedValue={difficulty}
                onValueChange={(val) => setDifficulty(val)}
              >
                <Picker.Item label="Easy" value="Easy" />
                <Picker.Item label="Moderate" value="Moderate" />
                <Picker.Item label="Hard" value="Hard" />
              </Picker>
            </View>
          </View>
        </View>

        {/* Date: dùng DatePicker */}
        <View style={styles.field}>
          <Text style={styles.label}>Date * (MM/DD/YYYY)</Text>
          <TouchableOpacity onPress={() => setShowDatePicker(true)}>
            <View pointerEvents="none">
              <TextInput
                style={styles.input}
                placeholder="Select date"
                value={date}
                editable={false}
              />
            </View>
          </TouchableOpacity>
        </View>

        <View style={styles.switchRow}>
          <Text style={styles.switchLabel}>Parking available?</Text>
          <Switch
            value={hasParking}
            onValueChange={setHasParking}
          />
        </View>

        <LabeledInput
          label="Notes"
          value={description}
          onChangeText={setDescription}
          multiline
        />

        <View style={styles.buttonRow}>
          <TouchableOpacity
            style={[styles.btn, styles.primaryBtn]}
            onPress={confirmSave}
          >
            <Text style={styles.primaryText}>
              {mode === 'edit' ? 'Save Changes' : 'Add Hike'}
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.btn, styles.cancelBtn]}
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.cancelText}>Cancel</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* DateTimePicker */}
      {showDatePicker && (
        <DateTimePicker
          value={date ? new Date(date) : new Date()}
          mode="date"
          display={Platform.OS === 'ios' ? 'spinner' : 'default'}
          onChange={onChangeDate}
        />
      )}
    </ScrollView>
  );
}

function LabeledInput({ label, style, ...rest }) {
  return (
    <View style={[styles.field, style]}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        style={styles.input}
        {...rest}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  scroll: { flex: 1, backgroundColor: '#F1F2F6' },
  container: {
    padding: 16,
  },
  formTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  field: {
    marginBottom: 10,
  },
  label: {
    fontSize: 13,
    color: '#555',
    marginBottom: 4,
  },
  input: {
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#ddd',
    backgroundColor: '#fff',
    paddingHorizontal: 10,
    paddingVertical: 8,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  half: {
    flex: 1,
    marginRight: 6,
  },
  switchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 8,
  },
  switchLabel: {
    flex: 1,
    fontSize: 14,
  },
  buttonRow: {
    flexDirection: 'row',
    marginTop: 18,
  },
  btn: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: 20,
    alignItems: 'center',
    marginHorizontal: 4,
  },
  primaryBtn: {
    backgroundColor: PURPLE,
  },
  primaryText: {
    color: '#fff',
    fontWeight: '600',
  },
  cancelBtn: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ccc',
  },
  cancelText: {
    color: '#333',
    fontWeight: '500',
  },
  // khung cho Picker
  pickerWrapper: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    backgroundColor: '#fff',
    overflow: 'hidden',
    height: 40,
  },
});
