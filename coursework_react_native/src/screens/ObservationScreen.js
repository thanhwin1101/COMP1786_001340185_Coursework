// src/screens/ObservationScreen.js
import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  FlatList,
  Alert,
  Platform,
} from 'react-native';
import DateTimePicker, {
  DateTimePickerAndroid,
} from '@react-native-community/datetimepicker';

import {
  getObservationsByHike,
  insertObservation,
  updateObservation,
  deleteObservation,
} from '../storage/db';

const PURPLE = '#6C47FF';

// format dạng 2025-11-28 10:07
function formatDateTime(dateObj = new Date()) {
  const yyyy = dateObj.getFullYear();
  const mm = String(dateObj.getMonth() + 1).padStart(2, '0');
  const dd = String(dateObj.getDate()).padStart(2, '0');
  const hh = String(dateObj.getHours()).padStart(2, '0');
  const min = String(dateObj.getMinutes()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

// cố gắng parse chuỗi time từ DB về Date
function parseToDate(str) {
  if (!str) return new Date();
  const d = new Date(str);
  if (!isNaN(d.getTime())) return d;

  // TH chuỗi dạng "YYYY-MM-DD HH:mm"
  const m = str.match(/^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})/);
  if (m) {
    return new Date(
      Number(m[1]),
      Number(m[2]) - 1,
      Number(m[3]),
      Number(m[4]),
      Number(m[5])
    );
  }
  return new Date();
}

export default function ObservationScreen({ route }) {
  const { hikeId, hikeName } = route.params;
  const [obsList, setObsList] = useState([]);
  const [editing, setEditing] = useState(null);
  const [title, setTitle] = useState('');
  const [comment, setComment] = useState('');

  // time + picker
  const [timeStr, setTimeStr] = useState(formatDateTime());
  // dùng cho iOS, Android sẽ dùng API open()
  const [showTimePicker, setShowTimePicker] = useState(false);

  async function load() {
    const data = await getObservationsByHike(hikeId);
    setObsList(data);
  }

  useEffect(() => {
    load();
  }, [hikeId]);

  function startEdit(item) {
    setEditing(item);
    setTitle(item.title);
    setComment(item.comment ?? '');
    setTimeStr(item.time || formatDateTime());
  }

  function clearForm() {
    setEditing(null);
    setTitle('');
    setComment('');
    setTimeStr(formatDateTime());
  }

  async function save() {
    if (!title.trim()) {
      Alert.alert('Validation', 'Observation title is required');
      return;
    }

    if (!editing) {
      await insertObservation({
        hikeId,
        title: title.trim(),
        time: timeStr,
        comment: comment.trim(),
      });
    } else {
      await updateObservation({
        id: editing.id,
        title: title.trim(),
        time: timeStr,
        comment: comment.trim(),
      });
    }
    clearForm();
    await load();
  }

  function confirmDelete(item) {
    Alert.alert(
      'Delete observation',
      `Delete "${item.title}"?`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: async () => {
            await deleteObservation(item.id);
            await load();
          },
        },
      ]
    );
  }

  const renderItem = ({ item }) => (
    <View style={styles.obsCard}>
      <Text style={styles.obsTitle}>{item.title}</Text>
      <Text style={styles.obsTime}>{item.time}</Text>
      {item.comment ? (
        <Text style={styles.obsComment}>{item.comment}</Text>
      ) : null}

      <View style={styles.obsRow}>
        <TouchableOpacity
          style={[styles.obsBtn, styles.editBtn]}
          onPress={() => startEdit(item)}
        >
          <Text style={styles.editText}>Edit</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.obsBtn, styles.delBtn]}
          onPress={() => confirmDelete(item)}
        >
          <Text style={styles.delText}>Delete</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  // mở DateTimePicker
  function openTimePicker() {
    const current = parseToDate(timeStr);

    if (Platform.OS === 'android') {
      // 👉 dùng API imperative trên Android (không render component)
      DateTimePickerAndroid.open({
        value: current,
        mode: 'datetime',
        is24Hour: true,
        onChange: (_event, selectedDate) => {
          if (!selectedDate) return; // user cancel
          setTimeStr(formatDateTime(selectedDate));
        },
      });
    } else {
      // iOS: hiển thị component ở dưới
      setShowTimePicker(true);
    }
  }

  // onChange cho iOS (component)
  function onChangeDateTimeIOS(_event, selectedDate) {
    if (!selectedDate) {
      setShowTimePicker(false);
      return;
    }
    setTimeStr(formatDateTime(selectedDate));
    setShowTimePicker(false);
  }

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>Observations – {hikeName}</Text>

      <View style={styles.form}>
        <TextInput
          style={styles.input}
          placeholder="Observation title *"
          value={title}
          onChangeText={setTitle}
        />

        <Text style={styles.label}>Time</Text>
        <TouchableOpacity onPress={openTimePicker}>
          <View pointerEvents="none">
            <TextInput
              style={styles.input}
              value={timeStr}
              editable={false}
            />
          </View>
        </TouchableOpacity>

        <TextInput
          style={[styles.input, styles.commentInput]}
          placeholder="Comment (optional)"
          value={comment}
          onChangeText={setComment}
          multiline
        />

        <View style={styles.formRow}>
          <TouchableOpacity
            style={[styles.formBtn, styles.saveBtn]}
            onPress={save}
          >
            <Text style={styles.saveText}>
              {editing ? 'Save changes' : 'Add observation'}
            </Text>
          </TouchableOpacity>
          {editing && (
            <TouchableOpacity
              style={[styles.formBtn, styles.cancelBtn]}
              onPress={clearForm}
            >
              <Text style={styles.cancelText}>Cancel</Text>
            </TouchableOpacity>
          )}
        </View>
      </View>

      <FlatList
        style={styles.list}
        data={obsList}
        keyExtractor={item => item.id.toString()}
        renderItem={renderItem}
        ListEmptyComponent={
          <Text style={styles.empty}>No observations yet.</Text>
        }
      />

      {/* iOS: hiển thị component DateTimePicker */}
      {Platform.OS === 'ios' && showTimePicker && (
        <DateTimePicker
          value={parseToDate(timeStr)}
          mode="datetime"
          display="spinner"
          onChange={onChangeDateTimeIOS}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F1F2F6',
    padding: 12,
  },
  heading: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  form: {
    backgroundColor: '#fff',
    padding: 10,
    borderRadius: 12,
  },
  label: {
    fontSize: 13,
    color: '#555',
    marginBottom: 4,
    marginLeft: 2,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    paddingHorizontal: 8,
    paddingVertical: 6,
    marginBottom: 8,
    backgroundColor: '#fff',
  },
  commentInput: {
    minHeight: 60,
  },
  formRow: {
    flexDirection: 'row',
  },
  formBtn: {
    flex: 1,
    paddingVertical: 8,
    borderRadius: 20,
    alignItems: 'center',
    marginHorizontal: 4,
  },
  saveBtn: {
    backgroundColor: PURPLE,
  },
  saveText: {
    color: '#fff',
    fontWeight: '600',
  },
  cancelBtn: {
    borderWidth: 1,
    borderColor: '#ccc',
    backgroundColor: '#fff',
  },
  cancelText: {
    color: '#333',
    fontWeight: '500',
  },
  list: {
    marginTop: 10,
  },
  obsCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 10,
    marginVertical: 4,
  },
  obsTitle: {
    fontWeight: '600',
  },
  obsTime: {
    fontSize: 11,
    color: '#777',
  },
  obsComment: {
    marginTop: 4,
  },
  obsRow: {
    flexDirection: 'row',
    marginTop: 8,
  },
  obsBtn: {
    flex: 1,
    borderRadius: 20,
    paddingVertical: 6,
    alignItems: 'center',
    marginHorizontal: 4,
  },
  editBtn: {
    borderWidth: 1,
    borderColor: '#333',
  },
  editText: {
    color: '#333',
  },
  delBtn: {
    backgroundColor: '#DC3545',
  },
  delText: {
    color: '#fff',
    fontWeight: '600',
  },
  empty: {
    textAlign: 'center',
    marginTop: 20,
    color: '#777',
  },
});
