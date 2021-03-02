package part1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public abstract class MyList<T> implements AdvancedList<T>, AuthorHolder{

    private int noOfElements = 0;

    Object[] objects = new Object[10];


    @Override
    public AdvancedList<T> shuffle() {
        Collections.shuffle(Arrays.asList(objects));
        return null;
    }

    @Override
    public  AdvancedList<T> sort(int i, int i1, Comparator<T> comparator) {

        sort(0, noOfElements - 1, comparator);
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public class sort implements part1.SimpleList<T> {
        Comparable[] a; // сортируемый массив
        Threds[] threads; //массив потоков
        int N = 0; //количество потоков(задаётся на входе)
        int threadNum = 0;//номер потока, который используем в данный момент(индекс в массиве threads)
        int left = 0, right = 0; //левая и правая граница сортируемой части массива

        public class Threds extends Thread { //класс для инициализации потоков
            int l;
            int r;

            public Threds(int left, int rigth) {
                l = left;
                r = rigth;
            }

            public void run() {
                MergeSort(l, r, threadNum);
            } //вызываем рекурсивно мёрдж-сорт
        }

        public sort(int NumberOfThreads, Comparable[] array) {
            N = NumberOfThreads;
          //  threads = new Threds[N];
            a = array;
        }


        public void MergeSort(int l, int r, int tNum) {
            if (l >= r) //если левая граница больше либо равно  правой, то ничего не нужно сортировать, т.к. подмассив состоит из 1 элемента
                return;
            int mid = (l + r) / 2; //вычисляем середину подмассива
            if (N == 1) { //если число потоков равно 1, то обычная сортировка, которая кстати у меня работает
                MergeSort(l, mid, 0);
                MergeSort(mid + 1, r, 0);
            } else {
                if (tNum > N) // проверка, чтобы не выйти за границы массива(другими словами, вычисляем индекс потока в масcиве threads по модулю N)
                    tNum = N - tNum;
                int m = tNum, n = tNum + 1;
                // Threds t1 = new Threds(l,mid); //вариант, когда не нужно использовать дополнительный массив потоков, но как тогда контролировать используемое колличество потоков?
                // t1.start();
                threads[m] = new Threds(l, mid);
                threads[m].start(); //запускаем сортировку от левой части подмассива
                left = mid + 1;
                right = r;

                threads[n] = new Threds(mid + 1, r);
                threads[n].start();// запускаем сортировку от правой части подмассива
                Threds t2 = new Threds(mid+1, r);
                t2.start();
                try { //ждём, чтобы оба потока завершились, чтобы затем слить 2 части подмассива(левую и правую)
                    threads[m].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    threads[n].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mearge(a, l, mid, r); // сливаем
        }


        public void mearge(Comparable[] array, int l, int mid, int r) { //обычный алгоритм слияния
            Comparable[] buf = new Comparable[array.length];
            Comparable[] res = new Comparable[array.length]; //заводим промежуточный массив для перестраховки(например один поток не завершил работу, а другой пытается обратиться к array, записать в него что-то или считать)
            synchronized (array) {
                for (int i = 0; i < array.length; i++) {
                    buf[i] = array[i];
                    res[i] = array[i];
                }
            }
            int i = l, j = mid + 1;
            for (int k = l; k <= r; k++) {
                if (i > mid) {
                    res[k] = buf[j];
                    j++;
                } else if (j > r) {
                    res[k] = buf[i];
                    i++;
                } else if (buf[j].compareTo(buf[i]) > 0) {
                    res[k] = buf[i];
                    i++;
                } else {
                    res[k] = buf[j];
                    j++;
                }
            }
            synchronized (array) {
                for (int t = 0; t < array.length; t++) {
                    array[t] = res[t];
                }
            }
        }


      //  @Override
        public String author() {
            return "MAD_MAX";
        }


        @Override
        public void add(T item) {
            if (noOfElements >= objects.length) objects = Arrays.copyOf(objects, objects.length * 2);
            objects[noOfElements] = item;
            noOfElements++;

        }


        @Override
        public void insert(int index, T item) throws Exception {
            if (index >= noOfElements) {
                throw new IndexOutOfBoundsException();
            }
            objects[index] = item;

        }

        @Override
        public void remove(int index) throws Exception {
            if (index >= noOfElements) throw new IndexOutOfBoundsException();
            for (int i = index; i < noOfElements; i++) {
                objects[i] = objects[i + 1];
            }
            noOfElements--;


        }

        @Override
        public Optional<T> get(int index) {
            if (index >= noOfElements) throw new IndexOutOfBoundsException();
            return Optional.of(((T) objects[index]));
        }

        @Override
        public int size() {
            return noOfElements;
        }

        @Override
        public void addAll(SimpleList<T> list) {
            for (int i = noOfElements; i < list.size(); i++) {
                add((T) list.get(i));
            }

        }

        @Override
        public int first(T item) {
            for (int i = 0; i < noOfElements; i++) {
                if (objects[i] == item)
                    return i;
            }
            return -1;
        }

        @Override
        public boolean last(T item) {
            for (int i = noOfElements - 1; i > 0; i--) {
                if (objects[i] == item)
                    return true;
            }
            return false;
        }

        @Override
        public boolean contains(T item) {
            for (int i = 0; i < noOfElements; i++) {
                if (objects[i] == item)
                    return true;
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return noOfElements == 0;
        }
    }
}
