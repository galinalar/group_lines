package group_line;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        List<String> listLines = parseFile(args[0]);
        List<Set<String>> groups = findGroups(listLines);
        List<Set<String>> sortedList = sort(groups);
        fillFile("groups.txt", sortedList);
        System.out.println(System.currentTimeMillis() - time);
    }

    private static List<String> parseFile(String fileName) throws IOException {
        try (FileReader input = new FileReader(fileName);
             BufferedReader bufRead = new BufferedReader(input)){
            String myLine;

            List<String> listLines = new ArrayList<>();
            while ( (myLine = bufRead.readLine()) != null)
            {
                listLines.add(myLine);
            }
            return listLines;
        }
    }

    private static void fillFile(String fileName, List<Set<String>> sortedList) throws IOException {
       try (FileWriter output = new FileWriter(fileName, Charset.forName("cp1251"));
            BufferedWriter bufWriter = new BufferedWriter(output)){
            for (int j = 0; j < sortedList.size(); j++)
            {
                if (sortedList.get(j).size()==1){
                    bufWriter.write("Групп с более чем одним элементом: " + j + "\n");
                    break;
                }
            }

            for (int i = 0; i < sortedList.size(); i++)
            {
                bufWriter.write("Группа " + (i + 1) + "\n");
                for (String line: sortedList.get(i)){
                    bufWriter.write(line + "\n");
                }
            }
       }
    }

    public static <T> List<Set<T>> sort(List<Set<T>> list) {
        list.sort((xs1, xs2) -> xs2.size() - xs1.size());
        return list;
    }

    private static List<Set<String>> findGroups(List<String> lines)
    {
        List<Map<String, Integer>> wordsToGroupsNumbers = new ArrayList<>();
        List<Set<String>> linesGroups = new ArrayList<>();
        Map<Integer, Integer> mergedGroupNumberToFinalGroupNumber = new HashMap<>();
        for (String line : lines)
        {
            String[] words = line.split(";");
            TreeSet<Integer> foundInGroups = new TreeSet<>();
            List<NewWord> newWords = new ArrayList<>();

            int count_com = 0;

            for (int i = 0; i < words.length; i++)
            {
                String word = words[i];

                if (wordsToGroupsNumbers.size() == i)
                    wordsToGroupsNumbers.add(new HashMap<>());

                if (word.equals("\"\""))
                    continue;

                for (char element : word.toCharArray()){
                    if (element == '\"') count_com++;
                }

                if (count_com > 2)
                    break;
                else count_com = 0;

                Map<String, Integer> wordToGroupNumber = wordsToGroupsNumbers.get(i);
                Integer wordGroupNumber = wordToGroupNumber.get(word);
                if (wordGroupNumber != null)
                {
                    while (mergedGroupNumberToFinalGroupNumber.containsKey(wordGroupNumber))
                        wordGroupNumber = mergedGroupNumberToFinalGroupNumber.get(wordGroupNumber);
                    foundInGroups.add(wordGroupNumber);
                }
                else
                {
                    newWords.add(new NewWord(word, i));
                }
            }

            if (count_com>2)
                continue;

            int groupNumber;

            if (foundInGroups.isEmpty())
            {
                groupNumber = linesGroups.size();
                linesGroups.add(new HashSet<>());
            }
            else
            {
                groupNumber = foundInGroups.first();
            }

            for (NewWord newWord : newWords)
                wordsToGroupsNumbers.get(newWord.position).put(newWord.value, groupNumber);

            for (int mergeGroupNumber : foundInGroups)
            {
                if (mergeGroupNumber != groupNumber)
                {
                    mergedGroupNumberToFinalGroupNumber.put(mergeGroupNumber, groupNumber);
                    linesGroups.get(groupNumber).addAll(linesGroups.get(mergeGroupNumber));
                    linesGroups.set(mergeGroupNumber, null);
                }
            }

            linesGroups.get(groupNumber).add(line);
        }
        linesGroups.removeAll(Collections.singleton(null));
        return linesGroups;
    }
}
